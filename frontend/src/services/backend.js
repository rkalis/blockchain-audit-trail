angular.module(
    'ecp-contactapp.services.backend', [])

    .service(
        'BackendService',
        ['PreferencesService', 'HttpService', 'AppConfig', '$filter',
        function(PreferencesService, HttpService, AppConfig, $filter) {

        var listAllKey = 'listAll' // for localStorage

        var instanceIdOf = function(href) {
            var n = href.lastIndexOf('/');
            var result = href.substring(n + 1);
            return result;
        }

        var dataProvenanceMessage = function(date) {
            return date ? "Data from " + $filter('date')(date, 'd MMM, HH:mm:ss') : ""
        }

        this.isOfflineEnabled = function() {
            return HttpService.isOfflineEnabled()
        }

        this.loadContactableList = function(onComplete, options) {

            var sort = function(respData) {
                respData.sort(function(a,b) {
                    // contact groups before contacts
                    if(a.type === "Contact Group" && b.type === "Contact") {
                        return -1
                    }
                    if(a.type === "Contact" && b.type === "Contact Group") {
                        return 1
                    }

                    // then by display order
                    if(a.displayOrder && !b.displayOrder) {
                        return -1
                    }
                    if(!a.displayOrder && b.displayOrder) {
                        return 1
                    }
                    if(a.displayOrder && b.displayOrder) {
                        return a.displayOrder - b.displayOrder
                    }

                    // then by name
                    return a.name.localeCompare(b.name)
                })
                return respData
            }

            HttpService.get(
                listAllKey,
                "/restful/services/ContactableViewModelRepository/actions/listAll/invoke",
                function(cachedData, date) {
                    onComplete(sort(cachedData), dataProvenanceMessage(date))
                },
                function(respData, date) {
                    var trimmedData = respData.map(
                        function(contactable){
                            contactable.$$instanceId = instanceIdOf(contactable.$$href)
                            delete contactable.$$href
                            delete contactable.$$title
                            delete contactable.notes
                            delete contactable.email
                            if(contactable.type === "Contact" && !contactable.company) {
                                contactable.company = "---"
                            }
                            if(contactable.type === "Contact Group" && !contactable.country) {
                                contactable.country = "---"
                            }
                            return contactable
                        }
                    )
                    return trimmedData
                },
                function(respData, date) {
                    onComplete(sort(respData), dataProvenanceMessage(date))
                },
                function(err) {
                    onComplete([], dataProvenanceMessage(null))
                },
                options
            )
        }

        var trimContactable = function(respData) {
              delete respData.$$href
              delete respData.$$instanceId
              delete respData.$$title
              respData.contactNumbers = respData.contactNumbers.map(
                  function(contactNumber){
                      delete contactNumber.$$instanceId
                      delete contactNumber.$$href
                      delete contactNumber.$$title
                      return contactNumber
                  }
              )
              respData.contactRoles = respData.contactRoles.map(
                  function(contactRole) {
                      delete contactRole.$$href
                      delete contactRole.$$title
                      contactRole.contact.$$instanceId = instanceIdOf(contactRole.contact.href)
                      delete contactRole.contact.href
                      delete contactRole.contact.rel
                      delete contactRole.contact.method
                      delete contactRole.contact.type
                      contactRole.contactGroup.$$instanceId = instanceIdOf(contactRole.contactGroup.href)
                      delete contactRole.contactGroup.href
                      delete contactRole.contactGroup.rel
                      delete contactRole.contactGroup.method
                      delete contactRole.contactGroup.type
                      return contactRole
                  }
              )
              return respData
        }

        this.loadContactables = function(instanceIds, onEachComplete, onAllComplete) {
            var urls = instanceIds.map(function(instanceId) {
                return "/restful/objects/org.incode.eurocommercial.contactapp.app.rest.v1.contacts.ContactableViewModel/" + instanceId
            })
            var num = 0;
            HttpService.getMany(
                instanceIds,
                urls,
                function(respData) {
                    var trimmedData = trimContactable(respData)
                    onEachComplete(++num, trimmedData)
                    return trimmedData
                },
                onAllComplete
            )
        }

        this.loadContactable = function(instanceId, onComplete, options) {
            HttpService.get(
                instanceId,
                "/restful/objects/org.incode.eurocommercial.contactapp.app.rest.v1.contacts.ContactableViewModel/" + instanceId,
                function(cachedData, date) {
                    onComplete(cachedData, dataProvenanceMessage(date))
                },
                trimContactable,
                function(respData, date) {
                    onComplete(respData, dataProvenanceMessage(date))
                    return respData
                },
                function(err, respData, date, resp) {
                    onComplete({}, dataProvenanceMessage(null))
                },
                options
            )
        }

        this.isCached = function(cacheKey) {
            return HttpService.isCached(cacheKey)
        }


    }])

;
