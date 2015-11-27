angular.module(
    'ecp-contactapp',
    [
    'ecp-contactapp.services.preferences',
    'ecp-contactapp.services.http',
    'ecp-contactapp.services.offline',
    'ecp-contactapp.services.backend',
    'ecp-contactapp.services.authentication',
    'ecp-contactapp.controllers.contacts',
    'ecp-contactapp.controllers.about',
    'ecp-contactapp.controllers.options',
    'ionic',
    'ngResource',
    'jett.ionic.filter.bar'])

    .value('AppConfig', {
        appPrefix: 'contactapp',
        baseUrl: "http://127.0.0.1:8080"   // TODO: move to preferences service ?
    })

    .config(
        ["$ionicConfigProvider",
        function($ionicConfigProvider){

        $ionicConfigProvider.tabs.position('bottom');
    }])

    .run(
        ["$ionicPlatform", "$rootScope", 'PreferencesService',
         function($ionicPlatform, $rootScope, PreferencesService) {
        $ionicPlatform.ready(function() {

            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
                cordova.plugins.Keyboard.disableScroll(true);
            }

            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                window.StatusBar.styleLightContent();
            }

            // utility methods for any view
            $rootScope.isUndefined = function (thing) {
                return thing === null || (typeof thing === "undefined");
            }
            $rootScope.isDefined = function (thing) {
                return !$rootScope.isUndefined(thing);
            }
            $rootScope.isDefinedWithLength = function (thing) {
                return $rootScope.isDefined(thing) && thing.length > 0
            }

            $rootScope.isNotProduction = function (thing) {
                return PreferencesService.preferences.environment.selected !== "Production"
            }

            $rootScope.ionicPlatform = {
                deviceInformation: ionic.Platform.device(),
                isWebView: ionic.Platform.isWebView(), // ie running in Cordova
                isIPad: ionic.Platform.isIPad(),
                isIOS: ionic.Platform.isIOS(),
                isAndroid: ionic.Platform.isAndroid(),
                isWindowsPhone: ionic.Platform.isWindowsPhone(),
                platform: ionic.Platform.platform(),
                platformVersion: ionic.Platform.version(),

                onDevice: ionic.Platform.isWebView() // true if on a device
            }


            // for debugging
            $rootScope.huzzah = function() {
                $ionicPopup.alert({
                      title: 'Huzzah',
                      template: 'it worked!'
                    });
            }

        });
    }])


    .config(
        ["$stateProvider", "$urlRouterProvider",
        function($stateProvider, $urlRouterProvider) {

    // Ionic uses AngularUI Router which uses the concept of states
    // Learn more here: https://github.com/angular-ui/ui-router
    // Set up the various states which the app can be in.
    // Each state's controller can be found in controllers.js
    $stateProvider

        .state('login', {
            url: '/login',
            templateUrl: 'templates/login.html',
            controller: 'LoginCtrl as ctrl'
        })

        .state('about', {
            cache: false,
            url: '/about',
            templateUrl: 'templates/about.html',
            controller: 'AboutCtrl as ctrl'
        })

        .state('tab', {
            cache: false,
            url: '/tab',
            templateUrl: 'templates/tabs.html',
            controller: 'OptionsCtrl as ctrl'
        })

        .state('tab.contactables', {
            cache: false,
            url: '/contactables',
            views: {
                'tab-contactables': {
                templateUrl: 'templates/contactable-list.html',
                controller: 'ContactablesCtrl as ctrl'
                }
            }
        })
        .state('tab.contactable-detail', {
            cache: false,
            url: '/contactables/:instanceId',
            views: {
                'tab-contactables': {
                    templateUrl: 'templates/contactable-detail.html',
                    controller: 'ContactableDetailCtrl as ctrl'
                }
            }
        })
        ;

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise(function ($injector, $location) {
            var $state = $injector.get("$state");
            $state.go("login");
        });
    }])

;
