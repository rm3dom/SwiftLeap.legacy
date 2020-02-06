var gulp = require('gulp');
var minifyInline = require('gulp-minify-inline');
var uglify = require('gulp-uglify-es').default;
var elm = require('gulp-elm');
var plumber = require('gulp-plumber');
var gutil = require("gulp-util");

var paths = {
    dest_assets: 'target/classes/static',
    dest_lib_assets: 'target/classes/static/lib',
    dest_templates: 'target/classes/static'
};

/*
gulp.task('elm-init', elm.init);
*/

/*
 * Common Tasks
 */
gulp.task('elm-init', elm.init);


gulp.task('copy-static-resources', function () {
    return gulp.src([
        'resources/static/**/*'
    ])
        .pipe(gulp.dest(paths.dest_assets));
});


gulp.task('do-resources', ['copy-static-resources']);


gulp.task('elm-bundle-admin', ['elm-init'], function () {
    return gulp.src('src/main/elm/Main.elm')
        .pipe(elm.bundle('admin.js').on('error', gutil.log))
        .pipe(uglify())
        .pipe(gulp.dest(paths.dest_lib_assets));
});


gulp.task('do-admin', ['elm-bundle-admin']);


gulp.task('default', ['do-resources', 'do-admin']);



