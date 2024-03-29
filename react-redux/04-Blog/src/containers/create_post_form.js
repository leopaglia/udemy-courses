import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { createPost } from '../actions';

class CreatePostForm extends Component {
	renderField(field) {
		const { input, label, meta: { touched, error } } = field;
		const inputClassName = `form-group ${touched && error ? 'has-danger' : ''}`;

		return (
			<div className={inputClassName}>
				<label>{label}</label>
				<input className="form-control" {...input}/>
				<div className="text-help">{touched ? error : ''}</div>
			</div>
		)
	}

	onSubmit(values) {
		this.props.createPost(values, () => {
			this.props.history.push('/');
		});
	}

	render() {
		const { handleSubmit } = this.props;
		return (
			<form onSubmit={handleSubmit(this.onSubmit.bind(this))}>
				<Field name="title" label="Title" component={this.renderField}/>
				<Field name="categories" label="Categories" component={this.renderField}/>
				<Field name="content" label="Content" component={this.renderField}/>
				<button className="btn btn-primary">Submit</button>
				<Link to="/" className="btn btn-danger">Cancel</Link>
			</form>
		)
	}
}

function validate(values) {
	const errors = {};

	if(!values.title) errors.title = "Enter a title!";
	if(values.title && values.title.length < 3) errors.title = "The title must be at least 3 characters long!";
	if(!values.categories) errors.categories = "Enter some categories!";
	if(!values.content) errors.content = "Enter some content!";

	return errors;
}

export default reduxForm({
	form: 'CreatePostForm',
	validate
})(connect(null, { createPost })(CreatePostForm));
