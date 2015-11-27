var gulp = require('gulp');
var gutil = require('gulp-util');
var bower = require('bower');
var concat = require('gulp-concat');
var sass = require('gulp-sass');
var minifyCss = require('gulp-minify-css');
var rename = require('gulp-rename');
var sh = require('shelljs');
var typescript = require('gulp-tsc');
var paths = {
  sass: ['./scss/**/*.scss'],
  src: ['./src/**/*.ts'],
  srcjs: ['./src/**/*.js'],
  wwwindex: ['./www/index.html'],
  wwwtemplates: ['./www/templates/**/*.html'],
  wwwimg: ['./www/img/**/*.png']
};

gulp.task('default', ['sass']);

gulp.task('compile', function() {
  gulp.src(paths.src)
    .pipe(typescript({
      emitError: false
    }))
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/js/'))
    .pipe(gulp.dest('www/js/'));

  gulp.src(paths.srcjs)
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/js/'))
    .pipe(gulp.dest('www/js/'));

  gulp.src(paths.wwwindex)
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/'));

  gulp.src(paths.wwwtemplates)
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/templates/'));

  gulp.src(paths.wwwimg)
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/img/'));
})

gulp.task('sass', function(done) {
  gulp.src('./scss/ionic.app.scss')
    .pipe(sass({
      errLogToConsole: true
    }))
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/css/'))
    .pipe(gulp.dest('www/css/'))
    .pipe(minifyCss({
      keepSpecialComments: 0
    }))
    .pipe(rename({
      extname: '.min.css'
    }))
    .pipe(gulp.dest('../backend/webapp/src/main/webapp/mobile/css/'))
    .pipe(gulp.dest('www/css/'))
    .on('end', done);
});

gulp.task('watch', function() {
  gulp.watch(paths.sass, ['sass']);
  gulp.watch(paths.src, ['compile']);
  gulp.watch(paths.srcjs, ['compile']);
});

gulp.task('install', ['git-check'], function() {
  return bower.commands.install()
    .on('log', function(data) {
      gutil.log('bower', gutil.colors.cyan(data.id), data.message);
    });
});

gulp.task('git-check', function(done) {
  if (!sh.which('git')) {
    console.log(
      '  ' + gutil.colors.red('Git is not installed.'),
      '\n  Git, the version control system, is required to download Ionic.',
      '\n  Download git here:', gutil.colors.cyan('http://git-scm.com/downloads') + '.',
      '\n  Once git is installed, run \'' + gutil.colors.cyan('gulp install') + '\' again.'
    );
    process.exit(1);
  }
  done();
});
