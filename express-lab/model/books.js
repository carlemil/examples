// Example books
var books = 
[{
  id: 'geb',
  title: 'Gödel, Escher, Bach: an Eternal Golden Braid',
  author: 'Douglas Hofstadter'
},
{
  id: 'bof',
  title: 'The Beginning of Infinity, Explanations That Transform the World',
  author: 'David Deutsch'
},
{
  id: 'zam',
  title: 'Zen and the Art of Motorcycle Maintenance',
  author: 'Robert Pirsig'
},
{
  id: 'fbr',
  title: 'Fooled by Randomness',
  author: 'Nicholas Taleb'
}];

function find(filterText) {
  if(!filterText) {
    return books;
  } else {
    return books.filter(function(book){ 
      return book.title.indexOf(filterText) > -1;
    });
  }
}

function findById(id) {
  return books.reduce(function(a, b){ 
    return a.id === id ? a: b;
  });
}

function deleteById(id) {
  var i = books.indexOf(findById(id));
  if(i != -1) {
    books.splice(i, 1);
  }
}

function put(id, book) {
	var i = books.indexOf(findById(id));
	if(i != -1) {
		books[i] = book;
	}
	return books;
}

module.exports = {
  find: find,
  findById: findById,
  deleteById: deleteById,
  put: put
};
