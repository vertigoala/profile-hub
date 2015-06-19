<div class="panel panel-default" ng-controller="UserAccessController as userCtrl" ng-form="UserForm" ng-cloak>
    <div class="panel-heading">
        <a name="accessControl">
            <h4>Access control</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <p>Assign users Admin or Editor roles to this profile collection.
            <ul>
                <li><b>Admin</b> - can edit the configuration for the entire collection, and can modify any profile in the collection.
                </li>
                <li><b>Editor</b> - can add and edit content on any profile page within this collection.</li>
                <li><b>Reviewer</b> - can view and add review comments on any profile page within this collection, but cannot create or modify profiles.
                </li>
            </ul>

            <div class="table-responsive">
                <table class="table table-striped" ng-show="userCtrl.users.length > 0">
                    <tr>
                        <th width="20%">Name</th>
                        <th width="20%">Email</th>
                        <th width="10%">Role</th>
                        <th width="35%">Notes</th>
                        <th width="15%"></th>
                    </tr>

                    <tr ng-repeat="user in userCtrl.users | orderBy:'name'">
                        <td>
                            {{ user.name }}
                        </td>
                        <td>
                            {{ user.email }}
                        </td>
                        <td>
                            {{ (userCtrl.roles | filter:user.role)[0].name }}
                        </td>
                        <td>
                            {{ user.notes }}
                        </td>
                        <td>
                            <button class="btn btn-link fa fa-trash-o color--red" title="Delete user"
                                    ng-click="userCtrl.deleteUser(user.userId, UserForm)"></button>
                            <button class="btn btn-link fa fa-edit" title="Edit user"
                                    ng-click="userCtrl.editUser(user.userId, UserForm)"></button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group">
                    <button class="btn btn-default" ng-click="userCtrl.addUser(UserForm)"><i
                            class="fa fa-plus"></i>  Add user</button>
                    <button class="btn btn-default" ng-click="userCtrl.reset(UserForm)">Reset</button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="userCtrl.save(UserForm)">
                    <span ng-show="!userCtrl.saving" id="saved"><span ng-show="UserForm.$dirty">*</span> Save</span>
                    <span ng-show="userCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>



    <script type="text/ng-template" id="addEditUserPopup.html">
    <div class="modal-header">
        <h3 class="modal-title">{{addUserCtrl.isNewUser ? 'Add User' : 'Edit User'}}</h3>
    </div>

    <div class="modal-body" ng-form="UserForm">
        <div class="row">
            <div class="span12">

                <div class="alert alert-danger" ng-show="addUserCtrl.error">{{addUserCtrl.error}}</div>

                <p ng-show="addUserCtrl.isNewUser">
                    To add users, search for them by <b>email address</b>. <br/>
                    <b>Note:</b> users need to be registered with the Atlas of Living Australia.
                </p>

                <div class="form-horizontal">
                    <div class="input-group" ng-show="addUserCtrl.isNewUser">
                        <input class="form-control" id="appendedInputButton" type="text"
                               ng-model="addUserCtrl.searchTerm"
                               name="searchTerm" ng-enter="addUserCtrl.userSearch()">
                        <span class="input-group-btn">
                            <button class="btn btn-default" type="button"
                                    ng-click="addUserCtrl.userSearch()">Search for user</button>
                        </span>
                    </div>
                </div>

                <div class="info padding-top-1" ng-show="addUserCtrl.user.userId">
                    <span>{{ addUserCtrl.user.name }}</span>
                    -
                    <span>{{ addUserCtrl.user.email }}</span>
                    <hr/>
                </div>

                <div class="row padding-top-1">
                    <div class="form-horizontal">
                        <div class="form-group">
                            <label for="role" class="col-sm-3 control-label">Role</label>

                            <div class="col-sm-8">
                                <select id="role" ng-model="addUserCtrl.user.role" required ng-required
                                        class="form-control"
                                        ng-options="role.key as role.name for role in addUserCtrl.roles">
                                    <option value="">-- select a role --</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="form-horizontal">
                        <div class="form-group">
                            <label for="notes" class="col-sm-3 control-label">Notes:</label>

                            <div class="col-sm-8">
                                <textarea id="notes" ng-model="addUserCtrl.user.notes" rows="4"
                                          class="form-control"></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="addUserCtrl.ok()"
                ng-disabled="!addUserCtrl.user.userId || !addUserCtrl.user.role">OK</button>
        <button class="btn btn-default" ng-click="addUserCtrl.cancel()">Cancel</button>
    </div>
    </script>

</div>