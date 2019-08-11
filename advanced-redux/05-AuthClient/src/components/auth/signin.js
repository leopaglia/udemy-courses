import React, { Component } from 'react';
import { connect } from 'react-redux';
import { reduxForm, Field } from 'redux-form';
import { signinUser } from '../../actions';

class Signin extends Component{
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
				<input type={type ? type : 'text'} className="form-control" {...input}/>
				<div className="text-help">{touched ? error : ''}</div>
			</fieldset>
		)
	}

	handleFormSubmit({ email, password }) {
		this.props.signinUser({ email, password });
	}

	render() {
		const { handleSubmit } = this.props;

		return (
			<form onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
				{this.renderError()}
				<Field name="email" label="Email" component={this.renderField}/>
				<Field name="password" type="password" label="Password" component={this.renderField}/>
				<button className="btn btn-primary">Sign in</button>
			</form>
		);
	}
}

function validate(values) {
	const errors = {};

	if(!values.email) errors.email = "Enter your email!";
	if(!values.password) errors.password = "Enter your password!";

	return errors;
}

const formOptions = {
	form: 'signin',
	validate
};

const signinForm = reduxForm(formOptions)(Signin);

function mapStateToProps(state) {
	return {
		errorMessage: state.auth.error
	}
}

export default connect(mapStateToProps, { signinUser })(signinForm);

