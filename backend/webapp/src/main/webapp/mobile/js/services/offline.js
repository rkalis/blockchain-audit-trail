angular.module(
    'ecp-contactapp.services.offline', [])

    // stores all data under a single key, so can enumerate all.
    .service(
        'OfflineService',
        ['AppConfig',
        function(AppConfig) {

        var service = this;

        var localStorageKey = AppConfig.appPrefix + ".data"

        // https://incodehq.atlassian.net/browse/ELI-31
        // the intention here is that we'll check if running in Cordova (only true for mobile apps),
        // and if so then will enable caching.
        //
        // the work required is to change the calls to window.localStorage[...] to instead use the
        // appropriate SQLite API calls.  Some setup will be needed in the bootstrapping run() method in app.js
        //
        // for more details, see, eg:
        // https://blog.nraboy.com/2014/11/use-sqlite-instead-local-storage-ionic-framework/
        //
        // var offlineEnabled = window.cordova && window.cordova.plugins.sqlDB

        // until we do that work, just hard-coded to true
        var offlineEnabled = true


        this.isOfflineEnabled = function() {
            return offlineEnabled
        }

        var internalGet = function() {
            if(offlineEnabled) {
                var storedStr = window.localStorage[localStorageKey]
                if(!storedStr) {
                    stored = {}
                    window.localStorage[localStorageKey] = JSON.stringify(stored)
                    return stored
                } else {
                    return JSON.parse(storedStr)
                }
            } else {
                return {}
            }
        }

        var internalPut = function(stored) {
            if(offlineEnabled) {
                window.localStorage[localStorageKey] = JSON.stringify(stored)
                _stored = stored
            }
        }

        // in-memory copy
        var _stored = internalGet();

        this.get = function(cacheKey) {
            var stored = internalGet()
            return stored[cacheKey]
        }

        this.put = function(cacheKey, resp) {
            var stored = internalGet()
            stored[cacheKey] = {
                resp: resp,
                date: new Date()
            }
            internalPut(stored)
        }

        this.putMany = function(cacheKeys, responses) {
            var stored = internalGet()
            var currentDate = new Date()
            for (var i = 0; i < cacheKeys.length; i++) {
                var cacheKey = cacheKeys[i]
                var resp = responses[i]
                if(resp) {
                    stored[cacheKey] = {
                        resp: resp,
                        date: currentDate
                    }
                }
            }
            internalPut(stored)
        }


        this.lookup = function(cacheKey) {
            return _stored[cacheKey]
        }

        this.isCached = function(cacheKey) {
            var lookedUp = service.lookup(cacheKey)
            var cached = lookedUp !== undefined
            return cached
        }

        this.count = function() {
            return Object.keys(_stored).length
        }

        this.clearCache = function() {
            internalPut({})
        }
    }])


;
