package au.org.ala.profile.hub

import groovy.json.JsonSlurper

class BHLController {

    def index() { }

    def pageLookup(){

        //API key = 1265b70b-3f49-4e4a-9f9b-a0e5b1033228
        def bhlApiKey = "1265b70b-3f49-4e4a-9f9b-a0e5b1033228"
        def pageId = params.pageId
        def js = new JsonSlurper()
        def pageMetadataUrl = "http://www.biodiversitylibrary.org/api2/httpquery.ashx?op=GetPageMetadata&format=json&pageid=${pageId}&apikey=${bhlApiKey}"

        println("pageMetadataUrl: " + pageMetadataUrl)

        def itemId = js.parseText(new URL(pageMetadataUrl).text).Result.ItemID

        //http://www.biodiversitylibrary.org/api2/httpquery.ashx?op=GetItemMetadata&apikey=1265b70b-3f49-4e4a-9f9b-a0e5b1033228&itemid=92098&format=json

        def itemMetadataUrl = "http://www.biodiversitylibrary.org/api2/httpquery.ashx?op=GetItemMetadata&format=json&itemid=${itemId}&apikey=${bhlApiKey}"

        println("itemMetadataUrl: " + itemMetadataUrl)

        response.setContentType("application/json")
//        def itemMetadata = new URL(itemMetadataUrl).text


        def titleId = js.parseText(new URL(itemMetadataUrl).text).Result.PrimaryTitleID

        def titleMetadataUrl = "http://www.biodiversitylibrary.org/api2/httpquery.ashx?op=GetTitleMetadata&format=json&titleid=${titleId}&apikey=${bhlApiKey}&items=t"

        def titleMetadata = new URL(titleMetadataUrl).text

        render titleMetadata
    }
}
