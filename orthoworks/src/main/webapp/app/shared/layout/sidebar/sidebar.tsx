import './sidebar.scss';

import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faHome,
  faThList,
  faUserCog,
  faImages,
  faUserAlt,
  faDigitalTachograph,
  faMoneyBill,
  faShoppingBag,
  faStore,
  faShoppingCart,
  faDollarSign,
} from "@fortawesome/free-solid-svg-icons";
import { NavItem, NavLink, Nav } from "reactstrap";
import classNames from "classnames";
import { Link } from "react-router-dom";

import SubMenu from "./submenu";

const SideBar = ({ isOpen, toggle, isAuthenticated, isAdmin }) => (
  <div className={classNames("sidebar", { "is-open": isOpen })}>
    <div className="side-menu">
      <Nav vertical className="list-unstyled pb-3">
        <NavItem>
            <NavLink tag={Link} to={"/"}>
                <FontAwesomeIcon icon={faHome} className='mr-2' />  
                &nbsp; Dashboard  
            </NavLink> 
        </NavItem>
        {isAuthenticated && (
          <NavItem>
              <NavLink tag={Link} to={"/incentives"}>
                  <FontAwesomeIcon icon={faDollarSign} className='mr-2' />  
                  &nbsp; Token Incentives  
              </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={"/collection"}>
                <FontAwesomeIcon icon={faImages} className='mr-2' />  
                &nbsp; Collections  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={"/artist"}>
                <FontAwesomeIcon icon={faUserAlt} className='mr-2' />  
                &nbsp; Artist Collaborations  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={""}>
                <FontAwesomeIcon icon={faDigitalTachograph} className='mr-2' />  
                &nbsp; Analytics  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={"/transaction"}>
                <FontAwesomeIcon icon={faMoneyBill} className='mr-2' />  
                &nbsp; Transactions  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={""}>
                <FontAwesomeIcon icon={faShoppingBag} className='mr-2' />  
                &nbsp; Virtual Merch  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={""}>
                <FontAwesomeIcon icon={faStore} className='mr-2' />  
                &nbsp; Virtual Storefront  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <NavItem>
            <NavLink tag={Link} to={""}>
                <FontAwesomeIcon icon={faShoppingCart} className='mr-2' />  
                &nbsp; Buy Property  
            </NavLink> 
          </NavItem>)
        }
        {isAuthenticated && (
          <SubMenu title="&nbsp; Entities" icon={faThList} items={submenus[0]} />
        )}
        {isAuthenticated && isAdmin && (
          <SubMenu title="&nbsp; Administration" icon={faUserCog} items={submenus[1]} />
        )}
      </Nav>
    </div>
  </div>
);

const submenus = [
  [
    {
      title: "Transaction",
      target: "/transaction",
    },
    {
      title: "Address",
      target: "/address",
    },
    {
      title: "Data",
      target: "/data",
    },
    {
      title: "Customer",
      target: "/customer",
    },
    {
      title: "Collection",
      target: "/collection",
    },
    {
      title: "Art",
      target: "/art",
    },
    {
      title: "Output",
      target: "/output",      },
    {
      title: "Collector",
      target: "/collector",
    },
    {
      title: "Notification",
      target: "/notification",
    },
    {
      title: "Invoice",
      target: "/invoice",
    },
    {
    title: "Artist",
      target: "/artist",
    },
    {
      title: "Brand",
      target: "/brand",
    },
    {
      title: "Brand Category",
      target: "/brand-category",
    },
  ],
  [
    {
      title: "Gateway",
      target: "/admin/gateway",
    },
    {
      title: "User Management",
      target: "/admin/user-management",
    },
    {
      title: "Metrics",
      target: "/admin/metrics",
    },
    {
      title: "Health",
      target: "/admin/health",
    },
    {
      title: "Logs",
      target: "/admin/logs",
    },
    {
      title: "Configuration",
      target: "/admin/configuration",
    },
    {
      title: "API",
      target: "/admin/docs",
    },
    {
      title: "Database",
      target: "",
    },
  ],
];

export default SideBar;
