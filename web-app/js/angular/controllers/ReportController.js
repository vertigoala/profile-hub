/**
 * Controller for handling reports
 */
profileEditor.controller('ReportController', function (profileService, util, config, messageService) {
    var self = this;

    self.opusId = util.getEntityId("opus");

    self.pageSize = 25;

    self.reports = [
        {id: "mismatchedNames", name: "Mismatched Names"},
        {id: "draftProfiles", name: "Draft Profiles"}
    ];

    self.selectedReport = null;
    self.reportData = null;

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

        var future = profileService.loadReport(self.opusId, self.selectedReport.id, self.pageSize, offset);

        future.then(function(data) {
            self.reportData = data;
        }, function() {
            messageService.alert("An error occurred while producing the report.");
        })
    };

});