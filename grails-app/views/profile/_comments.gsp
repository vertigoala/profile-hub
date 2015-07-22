<div ng-controller="CommentController as commentCtrl">
    <a name="{{commentCtrl.readonly() ? 'view_' : 'edit_'}}comments"></a>

    <div class="panel panel-default" ng-cloak>
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading">Comments</h4>
                </div>
            </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-12" ng-if="!specCtrl.readonly()">
                    <div ng-repeat="comment in commentCtrl.comments | orderBy:'dateCreated'"
                         ng-init="path = [$index]" class="comment">
                        <hr ng-if="!$first" class="comment-divider"/>
                        <ng-include src="'commentContent.html'"/>
                    </div>

                    <div ng-if="commentCtrl.currentComment && !commentCtrl.currentComment.uuid && !commentCtrl.currentComment.parentCommentId">
                        <div text-angular text-angular-name="comment" ng-model="commentCtrl.currentComment.text"
                             ta-toolbar="{{richTextToolbarSimple}}"></div>

                        <div class="row pull-right">
                            <div class="col-sm-12">
                                <button class="btn btn-primary"
                                        ng-click="commentCtrl.saveComment(path)">Save comment</button>
                                <button class="btn btn-warning" ng-click="commentCtrl.cancel()">Cancel</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <div class="row">
                <div class="col-md-12">
                    <button class="btn btn-default" ng-click="commentCtrl.addComment()"><i
                            class="fa fa-plus"></i>&nbsp;Add comment</button>
                </div>
            </div>
        </div>
    </div>

    <script type="text/ng-template" id="commentContent.html">
    <div class="padding-left-1 comment-wrapper">
        <div class="row">
            <div class="col-md-4 time">
                {{comment.dateCreated | date: 'dd/MM/yyyy HH:mm'}}
            </div>

            <div class="col-md-4 author">
                {{comment.author.name}}
            </div>

            <g:if test="${params.isOpusReviewer}">
                <div class="col-md-4"
                     ng-if="${params.isOpusEditor} || comment.author.userId == '${request.userPrincipal?.attributes?.userid}'">
                    <button class="btn btn-link fa fa-trash-o color--red" ng-click="commentCtrl.deleteComment(path)"
                            title="Delete comment"></button>
                    <button class="btn btn-link fa fa-edit" ng-click="commentCtrl.editComment(path)"
                            title="Edit comment"></button>
                </div>
            </g:if>
        </div>

        <div class="row">
            <div class="col-md-12 ">
                <div ta-bind ng-model="comment.text" ng-if="comment.uuid != commentCtrl.currentComment.uuid"></div>
            </div>
        </div>

        <div class="row">
            <div class="col-md-2">
                <button class="btn btn-link fa fa-reply" ng-click="commentCtrl.reply(comment.uuid)"
                        ng-if="commentCtrl.currentComment.parentCommentId != comment.uuid && comment.uuid != commentCtrl.currentComment.uuid">&nbsp;Reply</button>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div ng-if="commentCtrl.currentComment && (comment.uuid == commentCtrl.currentComment.uuid || commentCtrl.currentComment.parentCommentId == comment.uuid)">
                    <div text-angular text-angular-name="comment" ng-model="commentCtrl.currentComment.text"
                         ta-toolbar="{{richTextToolbarSimple}}"></div>

                    <div class="row pull-right">
                        <div class="col-sm-12">
                            <button class="btn btn-primary"
                                    ng-click="commentCtrl.saveComment(path)">Save comment</button>
                            <button class="btn btn-warning" ng-click="commentCtrl.cancel()">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div ng-repeat="comment in comment.children | orderBy:'dateCreated'"
             ng-init="path = path.concat($index)" class="comment">
            <hr class="comment-divider"/>
            <ng-include src="'commentContent.html'"/>
        </div>
    </div>
    </script>
</div>