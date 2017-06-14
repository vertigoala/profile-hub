<%@ page contentType="text/css;charset=UTF-8" defaultCodec="none" %>
body
, .backgroundcolor--pale-sky
, .navbar-default .navbar-nav a
, .nav-tabs > li.active > a
, .nav-tabs > li.active > a:hover
, .nav-tabs > li.active > a:focus
, .side-menu
, accordion > div.panel-group > div.panel > div.panel-collapse > div.panel-body
, .nav > li > a:focus
, .nav > li > a:hover
, .btn-default
, .btn-default:hover
, .dropdown-toggle
{
  background-color: ${mainBackgroundColour};
}
.color--pale-sky
{
  color: ${mainBackgroundColour};
}

/* Main Text colour */
body
, p
, li
, ul
, .color-madison
, .heading-large
{
    color: ${mainTextColour};
}
.background-madison {
  background-color: ${mainTextColour};
}

/* Call To Action colour */
.background-lochmara
, .jumbotron
, .btn-primary
, .pagination > .active > a
, .pagination > .active > span
, .pagination > .active > a:hover
, .pagination > .active > a:focus
, .pagination > .active > span:hover
, .pagination > .active > span:focus
{
  background-color: ${callToActionColour};
}

h4
, a
, .heading-large
, .color-lochmara
, .breadcrumb > .active
, .btn-default
, .btn-mini
, .embedded-sandbox .btn
, .embedded-sandbox h1
, footer p.lead
, .navbar-default .navbar-brand
, .pagination > li > a
, .pagination > li > span
, .profile-contributor-text
, .soc-envelope:hover .fa
{
  color: ${callToActionColour};
}

.heading-underlined
, .btn-default
, .btn-default
, .btn-mini
, .btn-primary
, .btn-primary
, .dropdown-toggle
, .embedded-sandbox .btn
, .heading-underlined
, .main-stats__stat
, .main-stats > div:last-child
, .main-stats .main-stats__stat
, .nav-tabs > li.active > a
, .nav-tabs > li.active > a:hover
, .nav-tabs > li.active > a:focus
, .pagination > li > a
, .pagination > li > span
, .pagination > .active > a
, .pagination > .active > span
, .pagination > .active > a:hover
, .pagination > .active > span:hover
, .pagination > .active > a:focus
, .pagination > .active > span:focus
, .pagination > .disabled > span
, .pagination > .disabled > span:hover
, .pagination > .disabled > span:focus
, .pagination > .disabled > a
, .pagination > .disabled > a:hover
, .pagination > .disabled > a:focus
, #resourceList input[type=text]
{
  border-color: ${callToActionColour};
}

#primary-nav.navbar
, .nav-tabs
{
  border-bottom-color: ${callToActionColour};
}

.main-footer-border
, .main-footer
{
  border-top-color: ${callToActionColour};
}

/* Call To Action Hover colour */
.background-venice-blue
, .btn-primary:hover
, .btn-primary:focus
, .btn-primary:active
, .open > .dropdown-toggle.btn-primary
{
  background-color: ${callToActionHoverColour};
}

.btn-default:hover
, .btn-primary:hover
, .btn-primary:focus
, .btn-primary.focus
, .btn-primary:active
, .btn-primary.active
, .btn-default:hover
, .nav-tabs > li.active > a
, .pagination > li > a:focus
, .pagination > li > a:hover
, .pagination > li > span:hover
, .pagination > li > span:focus
, .nav-tabs > li > a:hover
, .open > .dropdown-toggle.btn-primary
{
  border-color: ${callToActionHoverColour};
}

.color-venice-blue
, a:hover
, a:focus
, .btn-default:hover
, .navbar-default .navbar-nav > .active > a
, .navbar-default .navbar-nav > li > a:hover
, .navbar-default .navbar-brand:hover
, .nav-tabs > li.active > a
, .btn-default:hover
, .pagination > li > a:hover
, .pagination > li > a:focus
, .pagination > li > span:hover
, .pagination > li > span:focus
, .nav-tabs > li.active > a
{
  color: ${callToActionHoverColour};
}

/* Header text colour */
input.form-control:focus + ul.dropdown-menu + span.input-group-btn > .btn-default
, input.form-control:focus + span.input-group-btn > .btn-default
, .header-control .dropdown-toggle
, .header-control .dropdown-toggle:hover
, .header-control input
, .header-control input:focus
{
  border-color: ${headerTextColour};
}
.navbar-default .navbar-nav a
, .navbar-default .navbar-nav > li > a
, .btn-default
, .dropdown-toggle
{
  color: ${headerTextColour};
}

/* Footer Background colour */

footer {
  background-color: ${footerBackgroundColour};
}

/* Footer Text colour */
footer
, .main-footer a
, .main-footer a:hover
, .main-footer a:focus
, .main-footer a > i
{
  color: ${footerTextColour};
}

@media only screen and (min-width: 992px){
  .border-left{
    border-left-color: ${footerTextColour};
  }
}