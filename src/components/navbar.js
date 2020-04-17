import React from 'react';
import { Navbar, Nav, NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { Session } from 'bc-react-session';
import { Routes } from '../routes';
import { FindRoutesURL, RouteURL } from '../router';
import './navbar.css';

export class BlogNavBar extends React.Component {
    constructor (props) {
        super(props);

        this.state = {
            logged_in: false
        };

        this.routes = Routes;

        this.loggedIn = this.loggedIn.bind(this);
    }

    componentDidMount () {
        Session.onChange(this.loggedIn);

        const session = Session.get();

        if (session.isValid) {
            this.setState({
                logged_in: true
            });
        }
        else {
            this.setState({
                logged_in: false
            });
        }
    }

    loggedIn (session) {
        // Because the navbar is in a separate element, I have to do this. :/
        window.location.reload(false);
    }

    render () {
        return (
            <div className="blog-nb-container">
                <Navbar className="blog-nav-bar" color="dark" dark>
                    <NavbarBrand href={(() => new FindRoutesURL('Home').get())()}>
                        {process.env.REACT_APP_BLOG_TITLE}
                    </NavbarBrand>
                    <Nav>
                        {this.routes.map((route, index) => {
                            const rt = route;

                            // Mutate navigability based on user session status
                            if (rt.name === 'Login' && this.state.logged_in) {
                                rt.navTo = false;
                            }
                            else if (rt.name === 'Logout' && this.state.logged_in) {
                                rt.navTo = true;
                            }

                            // Don't render non-navigable links
                            if (!rt.navTo) {
                                return null;
                            }

                            return (
                                <NavItem key={index}>
                                    <NavLink href={(() => new RouteURL(rt.path).get())()}>
                                        {rt.name}
                                    </NavLink>
                                </NavItem>
                            );
                        })}
                    </Nav>
                </Navbar>
                <p className="blog-blurb">{process.env.REACT_APP_BLOG_BLURB}</p>
            </div>
        );
    }
}
