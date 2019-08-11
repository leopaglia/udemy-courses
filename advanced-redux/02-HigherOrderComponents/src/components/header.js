import React, { Component } from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { authenticate } from '../actions';

class Header extends Component {
	login() {
		this.props.authenticate(true);
	}

	logout() {
		this.props.authenticate(false);
	}

	authButton() {
		return this.props.authenticated ?
			<button onClick={this.logout.bind(this)}>Log out</button> :
			<button onClick={this.login.bind(this)}>Log in</button>;
	}

	render() {
		return (
			<nav className="navbar navbar-light">
				<ul className="nav navbar-nav">
					<li className="nav-item">
						<Link to="/">Home</Link>
					</li>
					<li className="nav-item">
						<Link to="/resources">Resources</Link>
					</li>
					<li className="nav-item">
						{this.authButton()}
					</li>
				</ul>
			</nav>
		);
	}
}

function mapStateToProps({ authenticated }) {
	return { authenticated };
}

export default connect(mapStateToProps, { authenticate })(Header);
