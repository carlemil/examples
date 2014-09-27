
var request = require('supertest');
var expect = require('chai').expect;
var sinon = require('sinon');
var express = require('express');
var routes = require('../../lib/routes/books');
var books = require('../../model/books.js');

var app = express();
app.use('/books', routes);

describe('GET /books', function() {
    before(function() {
        // sinon.spy(Model, 'generate');
    });

    it('responds with books', function(done) {
        request(app)
            .get('/books')
            .expect('Content-Type', /json/)
            .expect(200)
            .end(function(err, res){
                if (err) throw err;
                expect(res.body).to.deep.equal(books.find());
                done();
            });
    });

    it('responds with book with id bof', function(done) {
        request(app)
            .get('/books/bof')
            .expect('Content-Type', /json/)
            .expect(200)
            .end(function(err, res){
                if (err) throw err;
                expect(res.body).to.deep.equal(books.findById('bof'));
                done();
            });
    });

    var the = 'the';
    it('responds with books, with "'+the+'" in title', function(done) {
        request(app)
            .get('/books/?filter='+the)
            .expect('Content-Type', /json/)
            .expect(200)
            .end(function(err, res){
                if (err) throw err;
                expect(res.body).to.deep.equal(books.find(the));
                done();
            });
    });

    var bof = 'bof';
    it('delete book with id "'+bof, function(done) {
        request(app)
            .delete('/books/'+bof)
            .expect(200)
            .end(function(err, res){
                if (err) throw err;
            });
        request(app)
            .get('/books/')
            .expect('Content-Type', /json/)
            .expect(200)
            .end(function(err, res){
                if (err) throw err;
                expect(res.body).to.deep.equal(books.find());
                done();
            });
    });

    after(function() {
        // Model.generate.restore();
    });
});

