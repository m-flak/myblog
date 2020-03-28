import React from 'react';
import { Card, CardBody, CardHeader, CardTitle, CardText } from 'reactstrap';
import { LoginFormWithRouter } from '../components';
import './routes.css';

export class LoginRoute extends React.Component {
    render() {
        return (
            <div className="RoutePage">
              <div className="Layout-LR">
                <Card color="dark">
                    <CardBody>
                        <CardHeader><h4><i>Blogs need content...</i></h4></CardHeader>
                        <CardTitle><i>so why not login?</i></CardTitle>
                        <CardText tag="div">
                            <LoginFormWithRouter />
                        </CardText>
                    </CardBody>
                </Card>
              </div>
            </div>
        );
    }
}
