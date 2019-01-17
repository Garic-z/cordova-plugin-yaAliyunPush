 module.exports = function(context) {
     var path = context.requireCordovaModule('path'),
         fs = context.requireCordovaModule('fs'),
         shell = context.requireCordovaModule('shelljs'),
         projectRoot = context.opts.projectRoot;
    var platformRoot = path.join(projectRoot, 'platforms/android');

    var plugins = context.opts.plugins || [];
    // The plugins array will be empty during platform add
    if (plugins.length > 0 && plugins.indexOf('cordova-plugin-yaaliyunpush') === -1) {
        return;
    }

    var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');
    if (!fs.existsSync(manifestFile)) {
        manifestFile = path.join(platformRoot, 'app/src/main/AndroidManifest.xml');
    }
    console.log("platformRoot:" + manifestFile);
    if (fs.existsSync(manifestFile)) {

        fs.readFile(manifestFile, 'utf8', function(err, data) {
            if (err) {
                throw new Error('Unable to find AndroidManifest.xml: ' + err);
            }

            var appClass = 'com.ya.yaaliyunpush.MainApplication';

            if (data.indexOf(appClass) == -1) {

                var result = data.replace(/<application/g, '<application android:name="' + appClass + '"');

                fs.writeFile(manifestFile, result, 'utf8', function(err) {
                    if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
                })
                console.log('android application add [android:name="' + appClass + '"]');
            }
        });
    }

    var ConfigParser = null;
    try {
        ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
    } catch (e) {
        // fallback
        ConfigParser = context.requireCordovaModule('cordova-lib/src/configparser/ConfigParser');
    }
    var config = new ConfigParser(path.join(projectRoot, "config.xml")),
        packageName = config.android_packageName() || config.packageName();
    // replace dash (-) with underscore (_)
    packageName = packageName.replace(/-/g, "_");
    console.info("Running android-install.Hook: " + context.hook + ", Package: " + packageName + ", Path: " + projectRoot + ".");
    if (!packageName) {
        console.error("Package name could not be found!");
        return;
    }

    // android platform available?
    if (context.opts.cordova.platforms.indexOf("android") === -1) {
        console.info("Android platform has not been added.");
        return;
    }
    var targetDir = path.join(projectRoot, "platforms", "android", "app", "src", "main", "java", packageName.replace(/\./g, path.sep), "alipush");
    // create directory
    console.info(targetDir);
    shell.mkdir('-p', targetDir);
    var filename = 'AliPushActivity.java';
    if (['after_plugin_install'].indexOf(context.hook) === -1) {
        try {
            fs.unlinkSync(path.join(targetDir, filename));
            shell.rm('-f', targetDir);
        } catch (err) { }
    } else {
        // sync the content
        fs.readFile(path.join(context.opts.plugin.dir, 'src', 'android', filename), { encoding: 'utf-8' }, function (err, data) {
            if (err) {
                throw err;
            }
            data = data.replace(/__PACKAGE_NAME__/gm, packageName);
            fs.writeFileSync(path.join(targetDir, filename), data);
        });
    }
};