/**
 * This file/module contains all configuration for the build process.
 */
module.exports = {

    /**
     * Root for the SRC files
     */
    root: 'src/main/webapp/src',

    /**
     * The `build_dir` folder is where our projects are compiled during
     * development and the `compile_dir` folder is where our app resides once it's
     * completely built.
     */
    build_dir: '${target}/gui/build',
    compile_dir: '${target}/gui/bin',

    /**
     * This is a collection of file patterns that refer to our app code (the
     * stuff in `src/`). These file paths are used in the configuration of
     * build tasks. `js` is all project javascript, less tests. `ctpl` contains
     * our reusable components' (`src/common`) template HTML files, while
     * `atpl` contains the same, but for our app's code. `html` is just our
     * main HTML file, `less` is our main stylesheet, and `unit` contains our
     * app's unit tests.
     */
    app_files: {
        js: [ '${root}/**/*.js', '!${root}/**/*.spec.js', '!${root}/assets/**/*.js' ],
        jsunit: [ '${root}/**/*.spec.js' ],

        coffee: [ '${root}/**/*.coffee', '!${root}/**/*.spec.coffee' ],
        coffeeunit: [ '${root}/**/*.spec.coffee' ],

        atpl: [ '${root}/app/**/*.tpl.html' ],
        ctpl: [ '${root}/common/**/*.tpl.html' ],

        html: [ '${root}/index.html' ],
        less: '${root}/less/main.less'
    }

}