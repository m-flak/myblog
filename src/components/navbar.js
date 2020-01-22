import React from 'react';
import { Navbar, Nav, NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { Routes } from '../routes';
import { FindRoutesURL, RouteURL } from '../router';
import './navbar.css';

const REMOVE_ME_FAKE_LOGGED_IN = false;

export class BlogNavBar extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            logged_in: false,
        };
    }

    componentDidMount() {
        // TODO: ACTUALLY HANDLE ACTIVE SESSIONS ETC ETC
        this.setState({
            logged_in: REMOVE_ME_FAKE_LOGGED_IN,
        });
    }

    render() {
        return (
            <div className="blog-nb-container">
                <Navbar className="blog-nav-bar" color="dark" dark>
                    <NavbarBrand href={(() => new FindRoutesURL('Home').get())()}>
                        {process.env.REACT_APP_BLOG_TITLE}
                    </NavbarBrand>
                    <Nav>
                        {Routes.map((route, index) => {
                            let rt = route;

                            // Mutate navigability based on user session status
                            if (rt.name === 'Login' && this.state.logged_in) {
                                rt.navTo = false;
                            }
                            else if (rt.name === 'Logout' && !this.state.logged_in) {
                                rt.navTo = false;
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
