var gulp = require('gulp');
var gutil = require('gulp-util');
var clean = require('gulp-clean');
var minifyCss = require('gulp-minify-css');
var jshint = require('gulp-jshint');
var stylish = require('jshint-stylish');
var uglify = require('gulp-uglify');
var gulpIgnore = require('gulp-ignore');
var ngAnnotate = require('gulp-ng-annotate');
var sass = require('gulp-sass');
var karma = require('gulp-karma');

var paths = [
  './src/js/*.*',
  './src/css/*.*'
];

gulp.task('sass', function () {
  'use strict';

  return gulp.src('./src/scss/*.scss')
    .pipe(sass())
    .pipe(gulp.dest('./src/css'));
});

gulp.task('lint', function () {
  'use strict';

  return gulp.src('./src/js/*.js')
    .pipe(jshint({lookup: true}))
    .pipe(jshint.reporter(stylish))
    .pipe(jshint.reporter('fail'));
});

gulp.task('minify-css', ['sass'], function () {
  'use strict';

  return gulp.src('./src/css/*.css', {base: './src'})
    .pipe(minifyCss({keepBreaks: true}))
    .pipe(gulp.dest('./dist/'));
});

gulp.task('uglifyJS', ['lint'], function () {
  'use strict';

  return gulp.src(['./src/js/*.js'], {base: './src'})
    .pipe(gulpIgnore.exclude(['./src/js/*-spec.js']))
    .pipe(ngAnnotate())
    .pipe(uglify({
      outSourceMap: true
    }))
    .pipe(gulp.dest('./dist'));
});

gulp.task('copy', ['minify-css'], function () {
  'use strict';

  return gulp.src('./src/templates/*', {base: './src'})
    .pipe(gulp.dest('./dist'));
});

gulp.task('clean', function () {
  'use strict';

  return gulp.src('./dist/**/*.*')
    .pipe(clean());
});

gulp.task('test', function () {
  'use strict';

  return gulp.src(['src/**/*-spec.js'])
    .pipe(karma({
      configFile: 'src/karma.conf.js',
      action: 'run'
    }))
    .on('error', function (err) {
      console.log(gutil.colors.red('Error') + ': ' + err);

      throw err;
    });
});

gulp.task('build', ['clean', 'copy', 'uglifyJS']);

gulp.task('default', ['build']);
