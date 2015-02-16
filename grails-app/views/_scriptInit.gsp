var alaWsUrls = {
    profileServiceBaseUrl: "${grailsApplication.config.profile.service.url}",
    userdetailsBaseUrl: "${grailsApplication.config.userdetails.service.url}",
    imagesBaseUrl: "${grailsApplication.config.images.service.url}",
    collectoryUrl: "${grailsApplication.config.collectory.service.url}",
    bieBaseUrl: "${grailsApplication.config.bie.base.url}",
    listsBaseUrl: "${grailsApplication.config.lists.base.url}",
    bieBaseUrl: "${grailsApplication.config.bie.baseURL}",

    biocacheBaseUrl: "${grailsApplication.config.biocache.base.url}",
    biocacheSearchPath: "${grailsApplication.config.biocache.occurrence.search.path}",
    biocacheRecordPath: "${grailsApplication.config.biocache.occurrence.record.path}",
    biocacheInfoPath: "${grailsApplication.config.biocache.occurrence.info.path}",
    biocacheWmsPath: "${grailsApplication.config.biocache.wms.path}",

    bhlThumbUrl: "${grailsApplication.config.biodiv.libray.thumb.url}",
    bhlLookupUrl :"${createLink(controller: 'BHL', action: 'pageLookup')}",
    bhlUpdateUrl: "${createLink(controller: 'profile', action: 'updateBHLLinks')}",

    linksUpdateUrl: "${createLink(controller: 'profile', action: 'updateLinks')}",
    deleteAttributeUrl: "${createLink(mapping: 'deleteAttribute')}",
    updateAttributeUrl: "${createLink(controller: 'profile', action: 'updateAttribute')}",
    app: "${request.contextPath}"
}

function initialiseUrls() {
    profiles.init({
        urls: alaWsUrls
    });
}