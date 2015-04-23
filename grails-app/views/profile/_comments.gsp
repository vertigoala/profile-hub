<div ng-controller="CommentController as commentCtrl" class="bs-docs-example"
     data-content="Comments" ng-cloak>

    <div ng-repeat="comment in commentCtrl.comments | orderBy:'dateCreated'" class="row-fluid" ng-init="path = [$index]">
        <hr ng-if="!$first"/>
        <ng-include src="'commentContent.html'"/>
    </div>

    <div ng-if="commentCtrl.currentComment && !commentCtrl.currentComment.uuid && !commentCtrl.currentComment.parentCommentId">
        <div text-angular text-angular-name="comment" ng-model="commentCtrl.currentComment.text"
             ta-toolbar="{{richTextToolbarSimple}}"></div>

        <div class="row-fluid">
            <button class="btn btn-primary"
                    ng-click="commentCtrl.saveComment(path)">Save comment</button>
            <button class="btn btn-warning" ng-click="commentCtrl.cancel()">Cancel</button>
        </div>
    </div>

    <div class="row-fluid" ng-if="!commentCtrl.currentComment">
        <button class="btn btn-info" ng-click="commentCtrl.addComment()">Add comment</button>
    </div>





    <script type="text/ng-template" id="commentContent.html">
    <div class="row-fluid">
        <div class="span2">
            <b>{{comment.dateCreated | date: 'dd/MM/yyyy H:mm'}}</b>
        </div>

        <div class="span2">
            <b>{{comment.author.name}}</b>
        </div>

        <g:if test="${params.isOpusReviewer}">
            <div class="span2" ng-if="${params.isOpusEditor} || comment.author.userId == '${request.userPrincipal?.attributes?.userid}'">
                <button class="btn btn-link fa fa-trash-o" ng-click="commentCtrl.deleteComment(path)"
                        title="Delete comment"></button>
                <button class="btn btn-link fa fa-edit" ng-click="commentCtrl.editComment(path)"
                        title="Edit comment"></button>
            </div>
        </g:if>
    </div>

    <div class="row-fluid comment-panel subpanel">
        <div ta-bind ng-model="comment.text" ng-if="comment.uuid != commentCtrl.currentComment.uuid"></div>

        <button class="btn btn-link fa fa-reply" ng-click="commentCtrl.reply(comment.uuid)"
                ng-if="commentCtrl.currentComment.parentCommentId != comment.uuid && comment.uuid != commentCtrl.currentComment.uuid">&nbsp;Reply</button>

        <div ng-if="commentCtrl.currentComment && (comment.uuid == commentCtrl.currentComment.uuid || commentCtrl.currentComment.parentCommentId == comment.uuid)">
            <div text-angular text-angular-name="comment" ng-model="commentCtrl.currentComment.text"
                 ta-toolbar="{{richTextToolbarSimple}}"></div>

            <div class="row-fluid">
                <button class="btn btn-primary"
                        ng-click="commentCtrl.saveComment(path)">Save comment</button>
                <button class="btn btn-warning" ng-click="commentCtrl.cancel()">Cancel</button>
            </div>
        </div>

        <div ng-repeat="comment in comment.children | orderBy:'dateCreated'" class="row-fluid" ng-init="path = path.concat($index)">
            <hr/>
            <ng-include src="'commentContent.html'"/>
        </div>
    </div>
    </script>
</div>