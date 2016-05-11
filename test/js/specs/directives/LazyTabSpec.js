describe('Directive: LazyTab', function() {
    var $compile,
        $rootScope,
        $templateCache;

    beforeEach(module('profileEditor'));

    beforeEach(inject(function(_$compile_, _$rootScope_, _$templateCache_){
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $templateCache = _$templateCache_;
    }));

    it('will save content nested in a lazy-tab to the template cache', function() {
        var lazyTabContent = '<span>This is the tab contents</span>';
        // Compile a piece of HTML containing the directive
        var element = $compile("<tabset><tab>The first tab is active by default</tab><tab lazy-tab>"+lazyTabContent+"</tab></tabset>")($rootScope);

        expect($templateCache.get('lazy-tab-template-1')).toEqual(lazyTabContent);
    });

    it('will replace the tab contents with an ng-include and a loading indicator', function() {
        var lazyTabContent = 'This is the tab contents';
        // Compile a piece of HTML containing the directive.  Note that the first tab will be made active during
        // the digest cycle so we have to use the second tab for our test.
        var element = $compile("<tabset><tab>The first tab is active by default</tab><tab lazy-tab><span>"+lazyTabContent+"</span></tab></tabset>")($rootScope);

        $rootScope.$digest();

        // The tabset / tab directive template will create tabs with the classes below. The lazy tab is the second
        // tab in this case.
        var lazyTabContentNode = element.find(".tab-content .tab-pane")[1];

        expect(lazyTabContentNode.innerHTML).toContain('<span class="fa fa-spin fa-spinner"></span>');
        expect(lazyTabContentNode.innerHTML).not.toContain(lazyTabContent);
    });

    it('will replace text content with an ng-include and a loading indicator', function() {
        var lazyTabContent = 'This is the tab contents';
        // Compile a piece of HTML containing the directive.  Note that the first tab will be made active during
        // the digest cycle so we have to use the second tab for our test.
        var element = $compile("<tabset><tab>The first tab is active by default</tab><tab lazy-tab>"+lazyTabContent+"</tab></tabset>")($rootScope);

        expect($templateCache.get('lazy-tab-template-1')).toEqual(lazyTabContent);

        $rootScope.$digest();

        // The tabset / tab directive template will create tabs with the classes below. The lazy tab is the second
        // tab in this case.
        var lazyTabContentNode = element.find(".tab-content .tab-pane")[1];

        expect(lazyTabContentNode.innerHTML).toContain('<span class="fa fa-spin fa-spinner"></span>');
        expect(lazyTabContentNode.innerHTML).not.toContain(lazyTabContent);
    });

    it('will load the tab contents when the tab is made active', function() {

        var lazyTabContent = 'This is the tab contents';
        // Compile a piece of HTML containing the directive.  Note that the first tab will be made active during
        // the digest cycle so we have to use the second tab for our test.
        var element = $compile("<tabset><tab>The first tab is active by default</tab><tab lazy-tab><span>"+lazyTabContent+"</span></tab></tabset>")($rootScope);

        $rootScope.$digest();

        // The tabset / tab directive template will create tabs with the classes below. The lazy tab is the second
        // tab in this case.
        var lazyTabContentNode = element.find(".tab-content .tab-pane")[1];

        expect(lazyTabContentNode.innerHTML).toContain('<span class="fa fa-spin fa-spinner"></span>');
        expect(lazyTabContentNode.innerHTML).not.toContain(lazyTabContent);

        // Now activate the tab
        element.find('.nav-tabs a')[1].click();
        $rootScope.$digest();

        lazyTabContentNode = element.find(".tab-content .tab-pane")[1];

        expect(lazyTabContentNode.innerHTML).toContain(lazyTabContent);
        expect(lazyTabContentNode.innerHTML).not.toContain('<span class="fa fa-spin fa-spinner"></span>');

    });

    it('will not prevent the tab select callback from being made', function() {

        var scope = $rootScope.$new();
        scope.select = jasmine.createSpy();

        var lazyTabContent = 'This is the tab contents';
        // Compile a piece of HTML containing the directive.  Note that the first tab will be made active during
        // the digest cycle so we have to use the second tab for our test.
        var element = $compile("<tabset><tab>The first tab is active by default</tab><tab lazy-tab select='select()'><span>"+lazyTabContent+"</span></tab></tabset>")(scope);

        $rootScope.$digest();

        // Now activate the tab
        element.find('.nav-tabs a')[1].click();
        $rootScope.$apply();

        expect(scope.select).toHaveBeenCalled();
    });

});