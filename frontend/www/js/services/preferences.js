angular.module(
    'ecp-contactapp.services.preferences', [])


    .service(
        'PreferencesService',
        ['AppConfig', '$rootScope',
        function(AppConfig, $rootScope) {

        var service = this;

        service.preferences = {}

        //
        // preferences.environment
        //
        var environmentKey = AppConfig.appPrefix + ".preferences.environment"

        var defaultEnvironment = "Production"
        // var defaultEnvironment = "Development"

        if(!window.localStorage[environmentKey]) {
            window.localStorage[environmentKey] = defaultEnvironment
        }

        service.preferences.environment = {
            options: [
                {
                    name: "Development",
                    url: "http://localhost:8080"
                },
                {
                    name: "Test",
                    url: "http://10.0.0.5:8080"
                },
                {
                    name: "Production",
                    url: "https://contacts.ecpnv.com"
                }
            ],
            selected: window.localStorage[environmentKey]
        }


        service.urlForSelectedEnvironment = function() {
            return service.preferences.environment.options.find(
                    function(element) {
                        return element.name === service.preferences.environment.selected
                    }).url
        }

        service.updateEnvironment = function(environmentName) {
            service.preferences.environment.selected = environmentName
            window.localStorage[environmentKey] = environmentName
        }




    }])



;
