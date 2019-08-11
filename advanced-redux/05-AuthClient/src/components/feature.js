import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchSecretCode } from '../actions';

class Feature extends Component{
	componentWillMount() {
		this.props.fetchSecretCode();
	}

	renderMessage() {
		const { message } = this.props;
		return message
			? <p>{message}</p>
			: <p>LDOAING SPERR SCROT MSSAGU!!1</p>;
	}

	render() {
		return (
			<div>
				<p>SUM SCRETT FTEURE HEAREE1!!1!1ELEVEN!!!1</p>
				{this.renderMessage()}
			</div>
		);
	}
}

function mapStateToProps(state) {
	return state.message;
}

export default connect(mapStateToProps, { fetchSecretCode })(Feature);
