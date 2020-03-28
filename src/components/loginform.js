import React from 'react';
import {Session} from 'bc-react-session';
import { RouteURL } from '../router.js';
import { withRouter } from "react-router";
import { getFromBackend, postToBackend, encryptPassword } from '../util';
import './loginform.css';

export class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loginFailed: false,
            loginError: '',
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.userRef = React.createRef();
        this.passRef = React.createRef();
    }

    handleSubmit(event) {
        event.preventDefault();
        getFromBackend('/request', {}, (data) => {
            const pubKey = data;
            const rU = this.userRef.current;
            const rP = this.passRef.current;

            postToBackend('/login', {user: rU.value, pass: encryptPassword(pubKey, rP.value)}, (data) => {
                // Establish our 'Session'
                Session.start({
                    payload: {
                        userName: rU.value,
                        accessToken: data,
                    },
                });

                this.props.history.push(new RouteURL('/').get());
            })
            .catch((error) => {
                console.log(error.stack);
                this.setState({loginFailed: true});
                this.setState({loginError: error.toString()});
            });
        })
        .catch((error) => {
            console.log(error.stack);
            this.setState({loginFailed: true});
            this.setState({loginError: error.toString()});
        });
    }

    render() {
        let msg_display_class = (failed) => {
            if (failed) {
                return 'DisplayLoginFail';
            }
            return 'HideLoginFail';
        };

        return (
            <div className="LoginForm">
                <br />

                <h5 className={msg_display_class(this.state.loginFailed)}>Login Unsuccessful!</h5>
                <h5 className={msg_display_class(this.state.loginFailed)}>ERROR: {this.state.loginError}</h5>

                <form className="LoginFields" onSubmit={this.handleSubmit}>
                    <label htmlFor="username">User Name:&nbsp;</label>
                    <input ref={this.userRef} type="text" id="username" name="user" required minLength="7" maxLength="16" size="20" />
                    <label htmlFor="password">Password:&nbsp;</label>
                    <input ref={this.passRef} type="password" id="password" name="pass" required minLength="8" />
                    <input type="submit" value="Login" />
                </form>
            </div>
        );
    }
}
export const LoginFormWithRouter = withRouter(LoginForm);
