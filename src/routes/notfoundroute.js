import React from 'react';
import { Link } from 'react-router-dom';
import { Card, CardBody, CardHeader, CardTitle, CardText } from 'reactstrap';
import './routes.css';

export class NotFoundRoute extends React.Component {
    render () {
        return (
            <div className="RoutePage">
              <div className="Layout-NFR">
                <Card color="dark">
                    <CardBody>
                        <CardHeader><h4>Ooops!</h4></CardHeader>
                        <CardTitle><i>The requested page cannot be found!</i></CardTitle>
                        <CardText>
                            We&#39;re unable to find <code>{window.location.pathname}</code>.
                            The content you&#39;re looking for may no longer exist.
                            <br/>
                            Click <Link to="/">Here</Link> to return to the homepage.
                        </CardText>
                    </CardBody>
                </Card>
              </div>
            </div>
        );
    }
}
