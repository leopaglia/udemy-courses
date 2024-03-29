import React, { Component } from 'react';
import { connect } from 'react-redux';
import { reduxForm, Field } from 'redux-form';
import { signupUser } from '../../actions';

class Signup extends Component {
	renderError() {
		if(this.props.errorMessage) {
			return (
				<div className="alert alert-danger">
					<strong>Error!</strong> {this.props.errorMessage}
				</div>
			);
		}
	}

	renderField(field) {
		const { input, type, label, meta: { touched, error } } = field;
		const inputClassName = `form-group ${touched && error ? 'has-danger' : ''}`;

		return (
			<fieldset className={inputClassName}>
				<label>{label}:</label>
				<input type={type || 'text'} className="form-control" {...input}/>
				<div className="text-help">{touched ? error : ''}</div>
			</fieldset>
		)
	}

	handleFormSubmit({ email, password }) {
		this.props.signupUser({ email, password });
	}

	render() {
		const { handleSubmit } = this.props;

		return (
			<form onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
				{this.renderError()}
				<Field name="email" label="Email" component={this.renderField}/>
				<Field name="password" type="password" label="Password" component={this.renderField}/>
				<Field name="passwordConfirm" type="password" label="Confirm your password" component={this.renderField}/>
				<button className="btn btn-primary">Sign up</button>
			</form>
		);
	}
}

function validate(values) {
	const errors = {};

	if(!values.email) errors.email = "Enter an email!";
	if(!values.password) errors.password = "Enter a password!";
	if(!values.passwordConfirm) errors.passwordConfirm = "Confirm your password!";
	if(values.password !== values.passwordConfirm) {
		errors.password = "The passwords do not match!";
		errors.passwordConfirm = "The passwords do not match!";
	}

	return errors;
}

const formOptions = {
	form: 'signup',
	validate
};

const signupForm = reduxForm(formOptions)(Signup);

function mapStateToProps(state) {
	return {
		errorMessage: state.auth.error
	}
}

export default connect(mapStateToProps, { signupUser })(signupForm);
