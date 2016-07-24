'use strict'

let gulp = require('gulp');
let sass = require('gulp-sass');
let babel = require('gulp-babel');
let browserify = require('gulp-browserify');

// Default runner
gulp.task('default', ['html','tmpl','css','js','ext-html','ext-tmpl','ext-css','ext-js']);

// Web App
gulp.task('html', function() {
    gulp.src('./index.html')
    .pipe(gulp.dest('./public'));
});

gulp.task('tmpl', function() {
    gulp.src('./templates/*.html')
    .pipe(gulp.dest('./public'));
});

gulp.task('css', function() {
    gulp.src('./css/styles.scss')
    .pipe(sass())
    .pipe(gulp.dest('./public'));
});

gulp.task('js', function() {
  gulp.src('./js/app.js')
  .pipe(babel({
			presets: ['es2015']
		}))
  .pipe(browserify())
  .pipe(gulp.dest('./public'));
});

// Chrome Extension
gulp.task('ext-html', function() {
    gulp.src('./chrome-ext/yada.html')
    .pipe(gulp.dest('./chrome-ext/yada-ext'));
});

gulp.task('ext-tmpl', function() {
    gulp.src('./chrome-ext/templates/*.html')
    .pipe(gulp.dest('./chrome-ext/yada-ext'));
});

gulp.task('ext-css', function() {
    gulp.src('./chrome-ext/css/styles.scss')
    .pipe(sass())
    .pipe(gulp.dest('./chrome-ext/yada-ext/css'));
});

gulp.task('ext-js', function() {
  gulp.src('./chrome-ext/js/ext.js')
  .pipe(babel({
			presets: ['es2015']
		}))
  .pipe(browserify())
  .pipe(gulp.dest('./chrome-ext/yada-ext'));
});

gulp.task('watch', function() {
  gulp.watch('./css/*.scss', ['css']);
  gulp.watch('./index.html', ['html']);
  gulp.watch('./templates/*.html', ['tmpl']);
  gulp.watch('./js/*.js', ['js']);
  gulp.watch('./js/*/*.js', ['js']);

  gulp.watch('./chrome-ext/css/*.scss', ['ext-css']);
  gulp.watch('./chrome-ext/yada.html', ['ext-html']);
  gulp.watch('./chrome-ext/templates/*.html', ['ext-tmpl']);
  gulp.watch('./chrome-ext/js/*.js', ['ext-js']);
  gulp.watch('./chrome-ext/js/*/*.js', ['ext-js']);
});
