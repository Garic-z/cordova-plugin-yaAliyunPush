module.exports = function(context) {

    var fs = context.requireCordovaModule('fs'),
        path = context.requireCordovaModule('path');

    var platformRoot = path.join(context.opts.projectRoot, 'platforms/android');
    var plugins = context.opts.plugins || [];
    // The plugins array will be empty during platform add
    if (plugins.length > 0 && plugins.indexOf('cordova-plugin-yaaliyunpush') === -1) {
        return;
    }

    var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');

    if (!fs.existsSync(manifestFile)) {
        manifestFile = path.join(platformRoot, 'app/src/main/AndroidManifest.xml');
    }

    if (fs.existsSync(manifestFile)) {

        fs.readFile(manifestFile, 'utf8', function(err, data) {
            if (err) {
                throw new Error('Unable to find AndroidManifest.xml: ' + err);
            }

            var appClass = 'com.ya.aliyunpush.MainApplication';

            if (data.indexOf(appClass) != -1) {

                var result = data.replace('android:name="' + appClass + '"', '');

                fs.writeFile(manifestFile, result, 'utf8', function(err) {
                    if (err) throw new Error('Unable to write into AndroidManifest.xml: ' + err);
                })
                console.log('android application [android:name="' + appClass + '"] has removed');
            }
        });
    }
};