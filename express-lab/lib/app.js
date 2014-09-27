'use strict';

var express = require('express');
var statusRoute = require('./routes/status');
var booksRoute = require('./routes/books');
var bodyParser = require('body-parser');

var app = express();

app.use(bodyParser.json());


// Middleware and routes are added with use
app.use('/status', statusRoute);
app.use('/books', booksRoute);

app.get('/', function(req, res) {
    res.send('Up and running :). Check <a href="/status">status</a>');
});

module.exports = app;

var morgan = require('morgan');

app.use(morgan('dev'));
