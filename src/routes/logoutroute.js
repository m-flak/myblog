import React from 'react';
import { Redirect } from 'react-router-dom';
import { Session } from 'bc-react-session';
import { getFromBackend } from '../util';
import './routes.css';

export class LogoutRoute extends React.Component {
    constructor (props) {
        super(props);

        this.session = Session.get();
    }

    render () {
        /*****************************
         PERFORM THE LOGOUT HERE
         *****************************/
        const session = this.session;
        if (session.isValid) {
            const payload = Session.getPayload();

            if (payload !== null || payload !== undefined) {
                getFromBackend('/logout', { tok: payload.accessToken }, (data) => {
                    Session.destroy();
                })
                .catch((error) => {
                    Session.destroy();
                });
            }
        }

        return (
            <div className="RoutePage">
              <div className="Layout-LR">
                <Redirect to="/" />
             </div>
            </div>
        );
    }
}
