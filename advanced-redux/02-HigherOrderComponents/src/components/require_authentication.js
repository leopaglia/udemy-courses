import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

export default function(ComposedComponent) {
	class Authentication extends Component {
		componentWillMount() {
			if(!this.props.authenticated) {
				this.context.router.push('/');
			}
		}

		componentWillUpdate(nextProps) {
			if(!nextProps.authenticated) {
				this.context.router.push('/');
			}
		}

		render() {
			return <ComposedComponent {...this.props}/>;
		}
	}

	Authentication.contextTypes = {
		router: PropTypes.object
	};

	function mapStateToProps({ authenticated }) {
		return { authenticated };
	}

	return connect(mapStateToProps)(Authentication);
}
