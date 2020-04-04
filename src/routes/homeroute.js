import React from 'react';
import {Session} from 'bc-react-session';
import { AboutMe, Posts, MonthList, NewPost } from '../components';
import './routes.css';

export class HomeRoute extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            canPost: false,
            rowOffset: 0,
            updateDummy: 1,     // this is modified just to trigger re-render
        };

        this.handleCompose = this.handleCompose.bind(this);
    }

    componentDidMount() {
        let session = Session.get();
        if (session.isValid) {
            this.setState({canPost: true, rowOffset: 1});
        }
        else {
            this.setState({canPost: false, rowOffset: 0});
        }
    }

    handleCompose(wasSuccessful) {
        if (wasSuccessful) {
            // Should variate 1 -> 3, 3 -> 1
            var newVal = this.state.updateDummy ^ 2;
            this.setState({updateDummy: newVal});
        }
    }

    render() {
        return (
            <div className="RoutePage">
              <div className="Layout-HR">
                <NewPost onCompose={this.handleCompose} canPost={this.state.canPost} />
                <Posts update={this.state.updateDummy} row={this.state.rowOffset+1} />
                <AboutMe row={this.state.rowOffset+1} />
                <MonthList row={this.state.rowOffset+2} />
              </div>
            </div>
        );
    }
}
