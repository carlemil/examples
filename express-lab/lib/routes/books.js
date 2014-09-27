'use strict';

var express = require('express');
var books = require('../../model/books.js');

var router = express.Router();

router.get('/', function(req, res) {
  res.send(books.find(req.param('filter')));
});

router.get('/:id', function(req, res) {
    res.send(books.findById(req.params.id));
});

router.delete('/:id', function(req, res) {
    res.send(books.deleteById(req.params.id));
});

router.put('/:id', function(req, res) {
	    console.log('req.body ', req );
    res.send(books.put(req.params.id, req.body));
//res.send(books.find(req.param('filter')));
});

module.exports = router;
