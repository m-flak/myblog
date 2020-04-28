import React from 'react';
import { Redirect } from 'react-router-dom';
import { Session } from 'bc-react-session';
import { Card, CardBody, CardHeader, CardTitle, CardText } from 'reactstrap';
import { LoginForm } from '../components';
import './routes.css';

export class LoginRoute extends React.Component {
    constructor (props) {
        super(props);

        this.state = {
            authenticated: false
        };

        this.handleOnAuthenticated = this.handleOnAuthenticated.bind(this);
    }

    componentDidMount () {
        const session = Session.get();
        if (session.isValid) {
            this.setState({authenticated: true});
        }
    }

    handleOnAuthenticated () {
        this.setState({authenticated: true});
    }

    render () {
        if (this.state.authenticated) {
            return (
                <Redirect to="/" />
            );
        }

        return (
            <div className="RoutePage">
              <div className="Layout-LR">
                <Card color="dark">
                    <CardBody>
                        <CardHeader><h4><i>Blogs need content...</i></h4></CardHeader>
                        <CardTitle><i>so why not login?</i></CardTitle>
                        <CardText tag="div">
                            <LoginForm onAuthenticated={this.handleOnAuthenticated} />
                        </CardText>
                    </CardBody>
                </Card>
              </div>
            </div>
        );
    }
}
