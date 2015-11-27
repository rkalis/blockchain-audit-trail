angular.module(
    'ecp-contactapp.controllers.options', [])

    .controller('OptionsCtrl',
        ['$scope', 'BackendService', 'OfflineService', 'AuthService', 'PreferencesService', '$state', '$timeout',
        function($scope, BackendService, OfflineService, AuthService, PreferencesService, $state, $timeout) {

        var ctrl = this;

        ctrl.username = AuthService.username();
        ctrl.logout = function() {
            AuthService.logout();
            $state.go('login', {}, {reload: true});
        }

        ctrl.preferences = PreferencesService.preferences;

        ctrl.isOfflineEnabled = function() {
            return BackendService.isOfflineEnabled()
        }

        ctrl.downloadContacts = function() {
            OfflineService.clearCache()

            BackendService.loadContactableList(
                function(contactables, messageIfAny){
                    ctrl.message = "Downloading..."
                    ctrl.numberOfContacts = contactables.length

                    var instanceIds = contactables.map(function(contactable) {
                        return contactable.$$instanceId
                    })

                    BackendService.loadContactables(
                        instanceIds,
                        function(num, contactData){
                            $timeout(function() {
                                ctrl.message = contactData.name + " (" + num + " of " + contactables.length + ")"
                            })
                        },
                        function() {
                            $timeout(function() {
                                ctrl.message = null
                            })
                        }
                    )
                }
            )
        }

        ctrl.numberOfDownloadedContacts = function() {
            var count = OfflineService.count() - 1 // one for "listAll"
            return count > 0 ? count : 0
        }

        ctrl.removeDownloadedContacts = function() {
            OfflineService.clearCache()
            ctrl.message = null
        }

    }])

;

