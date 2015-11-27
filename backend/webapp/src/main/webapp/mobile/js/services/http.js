angular.module(
    'ecp-contactapp.services.http' , [])

    .service('HttpService',
            ['$q', '$http', '$ionicLoading', 'AppConfig', 'OfflineService',
            function($q, $http, $ionicLoading, AppConfig, OfflineService) {

        var service = this

        var isUndefined = function (thing) {
            return thing === null || (typeof thing === "undefined");
        }
        var useIonicLoading = function(options) {
            return isUndefined(options) || !options.suppressIonicLoading
        }

        this.isOfflineEnabled = function() {
            return OfflineService.isOfflineEnabled()
        }

        var headerMap = {
            'Accept': 'application/json;profile=urn:org.apache.isis/v1;suppress=true'
        }

        this.get = function(cacheKey, relativeUrl, onCached, onData, onOK, onError, options) {
            var url = AppConfig.baseUrl + relativeUrl
            var localStorageKey = AppConfig.appPrefix + "." + cacheKey
            var cached = OfflineService.get(cacheKey)
            if(cached) {
                // return the data we already have stored offline
                if(onCached) {
                    onCached(cached.resp.data, cached.date)
                }
            }
            // asynchronously populate the offline cache if we can
            var showSpinner = !cached && useIonicLoading(options)
            if(showSpinner) {
                $ionicLoading.show({
                     delay: 200
                 })
            }
            $http.get(
                url, {
                    headers: headerMap
                }
            )
            .then(
                function(resp) {
                    if(showSpinner) {
                        $ionicLoading.hide()
                    }
                    resp.data = onData(resp.data)
                    if(OfflineService.isOfflineEnabled()) {
                        OfflineService.put(cacheKey, resp)
                        var stored = OfflineService.get(cacheKey)
                        if(stored) {
                            onOK(stored.resp.data, stored.date)
                        }
                    } else {
                        onOK(resp.data, null) // suppress any message at end
                    }
                },
                function(err) {
                    if(showSpinner) {
                        $ionicLoading.hide()
                    }
                    if(onError && !cached) {
                        // unable to obtain any data, and wasn't previously cached
                        onError(err)
                    }
                }
            )
        }

        this.getMany = function(cacheKeys, relativeUrls, onData, onAllComplete) {

            var httpPromises = []
            for (var i = 0; i < cacheKeys.length; i++) {
                var cacheKey = cacheKeys[i]
                var localStorageKey = AppConfig.appPrefix + "." + cacheKey
                var url = AppConfig.baseUrl + relativeUrls[i]

                var httpPromise = $http.get(
                    url, {
                        headers: headerMap
                    }
                )
                .then(
                    function(resp) {
                        resp.data = onData(resp.data)
                        return resp
                    }
                )
                httpPromises.push(httpPromise)
            }

            $q.all(httpPromises.map(function(promise) {
                    return promise.then(
                        function(value) {
                            return value;
                        },
                        function(reason) {
                            return null;
                        }
                    );
                })
            )
            .then(
                function(responses) {
                    if(OfflineService.isOfflineEnabled()) {
                        OfflineService.putMany(cacheKeys, responses)
                    }
                    onAllComplete()
                }
            )

        }

        this.lookup = function(cacheKey) {
            return OfflineService.lookup(cacheKey)
        }

        this.isCached = function(cacheKey) {
            return OfflineService.isCached(cacheKey)
        }


    }])

;
