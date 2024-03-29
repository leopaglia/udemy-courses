import React, { Component } from 'react';
import { connect } from 'react-redux'
import { selectBook } from '../actions'
import { bindActionCreators } from 'redux'

class BookList extends Component {
	renderList() {
		const { books, selectBook } = this.props;

		return books.map(book => {
			return(
				<li key={book.title}
						onClick={() => selectBook(book)}
						className="list-group-item">
					{book.title}
				</li>
			)
		})
	}

	render() {
		return (
			<ul className="list-group col-sm-4">
				{this.renderList()}
			</ul>
		)
	}
}

function mapStateToProps(state) {
	return {
		books: state.books
	}
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({selectBook: selectBook}, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(BookList);
