import React, { Component } from 'react';
import { connect } from 'react-redux';
import * as actions from '../actions';

class CommentBox extends Component {
	constructor(props) {
		super(props);
		this.state = { comment: '' };
	}

	/**
	 * @param {Event} e
	 */
	handleChange(e) {
		this.setState({ comment: e.target.value });
	}

	/**
	 * @param {Event} e
	 */
	handleSubmit(e) {
		e.preventDefault();
		this.props.saveComment(this.state.comment);
		this.setState({ comment: '' });
	}

	render() {
		return (
			<form onSubmit={this.handleSubmit.bind(this)} className="comment-box">
				<textarea onChange={this.handleChange.bind(this)} value={this.state.comment}/>
				<button type="submit">Submit Comment</button>
			</form>
		);
	}
}

export default connect(null, actions)(CommentBox);
