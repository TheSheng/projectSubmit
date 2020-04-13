var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});
router.get('/login', function(req, res, next) {
  res.render('login', { title: 'Express' });
});
router.get('/chat', function(req, res, next) {
    res.render('chat', { title: 'Express' });
});
router.get('/user', function(req, res, next) {
    res.render('user', { title: 'Express' });
});
router.get('/notice', function(req, res, next) {
    res.render('notice', { title: 'Express' });
});
router.get('/project', function(req, res, next) {
    res.render('project', { title: 'Express' });
});
router.get('/chushen', function(req, res, next) {
    res.render('projectChushen', { title: 'Express' });
});
router.get('/fushen', function(req, res, next) {
    res.render('fushen', { title: 'Express' });
});
router.get('/zhongshen', function(req, res, next) {
    res.render('zhongshen', { title: 'Express' });
});
router.get('/show', function(req, res, next) {
    res.render('showproject', { title: 'Express' });
});
router.get('/noticeshow', function(req, res, next) {
    res.render('noticeShow', { title: 'Express' });
});
module.exports = router;
