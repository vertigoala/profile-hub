<hr/>
<div ng-controller="CommentController as commentCtrl" class="comments-panel">
    <navigation-anchor name="{{commentCtrl.readonly() ? 'view_' : 'edit_'}}comments" title="Comments"></navigation-anchor>

    <div class="panel panel-default" ng-cloak>
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading inline">Comments</h4>
                    <span class="pull-right small inline"><i class="fa fa-info-circle">&nbsp;</i>Comments can only be seen by profile Reviewers and Editors.</span>
                </div>
            </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-12" ng-if="!specCtrl.readonly()">
                    <div ng-repeat="comment in commentCtrl.comments | orderBy:'dateCreated'"
                         ng-init="path = [$index]" class="comment">
                        <hr ng-if="!$first" class="comment-divider"/>
                        <ng-include src="'commentContent.html'"></ng-include>
                    </div>

                    <div ng-if="commentCtrl.currentComment && !commentCtrl.currentComment.uuid && !commentCtrl.currentComment.parentCommentId">
                        <label for="commentText" class="screen-reader-label">Comment text</label>
                        <textarea id="commentText" ng-model="commentCtrl.currentComment.text" ckeditor="richTextFullToolbar" required="required"></textarea>

                        <div class="row pull-right">
                            <div class="col-sm-12 padding-top-1">
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
                <div data-ng-bind-html="comment.text | sanitizeHtml" ng-if="comment.uuid != commentCtrl.currentComment.uuid"></div>
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
                    <label for="commentText2" class="screen-reader-label">Comment text</label>
                    <textarea id="commentText2" ng-model="commentCtrl.currentComment.text" ckeditor="richTextFullToolbar" required="required"></textarea>

                    <div class="row pull-right">
                        <div class="col-sm-12 padding-top-1">
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
            <ng-include src="'commentContent.html'"></ng-include>
        </div>
    </div>
    </script>
</div>