<div class="well row-fluid" ng-controller="UserAccessController as userCtrl">
    <div class="span6">
        <h3>Access control</h3>
        <p>Assign users Admin or Editor roles to this profile collection.
        <ul>
            <li><b>Admin</b> - can edit content on this page and any profile in the collection</li>
            <li><b>Editor</b> - can edit content on any profile page with this collection. Can not edit this page.</li>
        </ul>

        <div class="subpanel">
            To add users, search for them by <b>email address</b>. <br/>
            <b>Note:</b> users will need to create their ALA account before they can be added.
        </p>

            <div class="input-append">
                <input class="span12" id="appendedInputButton" type="text" ng-model="userCtrl.searchTerm" name="searchTerm">
                <button class="btn" type="button" ng-click="userCtrl.userSearch()">Search for user</button>
            </div>
            <p class="info" ng-show="userCtrl.retrievedUser != null">
                <span>{{ userCtrl.retrievedUser.firstName }} {{ userCtrl.retrievedUser.lastName }}</span>
                -
                <span>{{ userCtrl.retrievedUser.email }}</span>
                <span style="padding-left:30px;">
                    <button class="btn" ng-click="userCtrl.addAdmin()"><i class="icon icon-plus"></i> Add admin</button>
                    <button class="btn" ng-click="userCtrl.addEditor()"><i class="icon icon-plus"></i> Add editor</button>
                </span>
            </p>
        </div>
        <p>
            <button class="btn btn-primary" ng-click="userCtrl.save()">Save</button>
        </p>
    </div>

    <div class="span6">
        <h4 ng-show="userCtrl.admins.length > 0">Admins</h4>
        <ul>
            <li ng-repeat="user in userCtrl.admins">
                <span>{{ user.displayName }}</span>
                -
                <span>{{ user.email }}</span>
                &nbsp;
                <button class="btn btn-danger" ng-click="userCtrl.remove('admins', $index)"><i class="icon-minus icon-white"></i></button>
            </li>
        </ul>

        <h4 ng-show="userCtrl.editors.length > 0">Editors</h4>
        <ul>
            <li ng-repeat="user in userCtrl.editors">
                <span>{{ user.displayName }}</span>
                -
                <span>{{ user.email }}</span>
                &nbsp;
                <button class="btn btn-danger" ng-click="userCtrl.remove('editors', $index)"><i class="icon-minus icon-white"></i></button>
            </li>
        </ul>
    </div>


</div>