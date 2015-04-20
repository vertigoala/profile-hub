<div class="well row-fluid" ng-controller="UserAccessController as userCtrl" ng-cloak ng-form="UserForm">
    <div class="span12">
        <h3>Access control</h3>

        <p>Assign users Admin or Editor roles to this profile collection.
        <ul>
            <li><b>Admin</b> - can edit the configuration for the entire collection, and can modify any profile in the collection.
            </li>
            <li><b>Editor</b> - can add and edit content on any profile page within this collection.</li>
            <li><b>Reviewer</b> - can view and add review comments on any profile page within this collection, but cannot create or modify profiles.
            </li>
        </ul>

        <table class="table table-striped" ng-show="userCtrl.users.length > 0">
            <tr>
                <th width="20%">Name</th>
                <th width="20%">User ID</th>
                <th width="20%">Role</th>
                <th width="25%">Notes</th>
                <th width="15%"></th>
            </tr>

            <tr ng-repeat="user in userCtrl.users | orderBy:'name'">
                <td>
                    {{ user.name }}
                </td>
                <td>
                    {{ user.userId }}
                </td>
                <td>
                    {{ (userCtrl.roles | filter:user.role)[0].name }}
                </td>
                <td>
                    {{ user.notes }}
                </td>
                <td>
                    <button class="btn btn-link fa fa-trash-o" title="Delete user" ng-click="userCtrl.deleteUser(user.userId, UserForm)"> Delete</button>
                    <button class="btn btn-link fa fa-edit" title="Edit user" ng-click="userCtrl.editUser(user.userId, UserForm)"> Edit</button>
                </td>
            </tr>
        </table>

        <button class="btn btn-info" ng-click="userCtrl.addUser(UserForm)">Add user</button>

        <div class="row-fluid">
            <hr/>
            <button class="btn btn-primary" ng-click="userCtrl.save(UserForm)">
                <span ng-show="!userCtrl.saving" id="saved"><span ng-show="UserForm.$dirty">*</span> Save</span>
                <span ng-show="userCtrl.saving" id="saving">Saving....</span>
            </button>
            <button class="btn btn-warning" ng-click="userCtrl.reset(UserForm)">Reset</button>
        </div>
    </div>



    <script type="text/ng-template" id="addEditUserPopup.html">
    <div class="modal-header">
        <h3 class="modal-title">{{addUserCtrl.isNewUser ? 'Add User' : 'Edit User'}}</h3>
    </div>

    <div class="modal-body" ng-form="UserForm">
        <div class="row-fluid">
            <div class="span12">

                <div class="alert alert-danger" ng-show="addUserCtrl.error">{{addUserCtrl.error}}</div>

                <p ng-show="addUserCtrl.isNewUser">
                    To add users, search for them by <b>email address</b>. <br/>
                    <b>Note:</b> users need to be registered with the Atlas of Living Australia.
                </p>

                <div class="input-append" ng-show="addUserCtrl.isNewUser">
                    <input class="span12" id="appendedInputButton" type="text" ng-model="addUserCtrl.searchTerm"
                           name="searchTerm" ng-enter="addUserCtrl.userSearch()">
                    <button class="btn" type="button" ng-click="addUserCtrl.userSearch()">Search for user</button>
                </div>

                <div class="info" ng-show="addUserCtrl.user.userId">
                    <span>{{ addUserCtrl.user.name }}</span>
                    -
                    <span>{{ addUserCtrl.user.userId }}</span>
                    <hr/>
                </div>

                <div class="row-fluid">
                    <label for="role">Role:</label>
                    <select id="role" ng-model="addUserCtrl.user.role" required ng-required
                            ng-options="role.key as role.name for role in addUserCtrl.roles">
                        <option value="">-- select a role --</option>
                    </select>
                </div>

                <div class="row-fluid">
                    <label for="notes">Notes:</label>
                    <textarea id="notes" ng-model="addUserCtrl.user.notes" rows="4" style="width:100%"></textarea>
                </div>
            </div>
        </div>

    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="addUserCtrl.ok()" ng-disabled="!addUserCtrl.user.userId || !addUserCtrl.user.role">OK</button>
        <button class="btn btn-warning" ng-click="addUserCtrl.cancel()">Cancel</button>
    </div>
    </script>

</div>