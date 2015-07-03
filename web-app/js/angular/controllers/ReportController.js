/**
 * Controller for handling reports
 */
profileEditor.controller('ReportController', function (profileService, util, config, messageService) {
    var self = this;

    self.opusId = util.getEntityId("opus");

    self.pageSize = 25;

    self.reports = [
        {id: "mismatchedNames", name: "Mismatched Names"},
        {id: "draftProfiles", name: "Draft Profiles"},
        {id: "mostRecentChange", name: "Most Recent Changes"}
    ];

    self.selectedReport = null;
    self.reportData = null;

    self.periods =[
        {id:'today', name: 'Today'},
        {id:'last7Days', name: 'Last 7 Days'},
        {id:'last30Days', name: 'Last 30 days'},
        {id:'custom', name: 'Custom', from: null, to: null}
    ]

    self.selectedPeriod = self.periods[0];

    self.contextPath = function() {
        return config.contextPath;
    };

    self.loadReport = function(reportId, offset) {
        self.selectedReport = null;

        angular.forEach(self.reports, function(report) {
            if (report.id === reportId) {
                self.selectedReport = report;
            }
        });

        if (offset === undefined) {
            offset = 0;
        }

        var future = profileService.loadReport(self.opusId, self.selectedReport.id, self.pageSize, offset, self.selectedPeriod.id, self.selectedPeriod.from, self.selectedPeriod.to);

        future.then(function(data) {
            self.reportData = data;
        }, function() {
            messageService.alert("An error occurred while producing the report.");
        })
    };

    self.setPeriod = function(p){
        self.selectedPeriod = p;
        switch (p.id){
            case 'custom':
                break;
            case 'last30Days':
            case 'last7Days':
            case 'today':
                self.loadReport(self.selectedReport.id, 0);
                break;
        }
    }

});