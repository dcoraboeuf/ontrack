Building the client side
========================

```
npm -g install grunt-cli karma bower
npm install
bower install
```

Then, to build in development mode (no compression):

```
grunt clean dev
```

To build in development mode and scan for changes:

```
grunt watch
```

To build for the production:

```
grunt clean prod
```

