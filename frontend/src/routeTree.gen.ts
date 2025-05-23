/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file was automatically generated by TanStack Router.
// You should NOT make any changes in this file as it will be overwritten.
// Additionally, you should also exclude this file from your linter and/or formatter to prevent it from being checked or modified.

// Import Routes

import { Route as rootRoute } from './routes/__root'
import { Route as AuthRouteImport } from './routes/_auth/route'
import { Route as IndexImport } from './routes/index'
import { Route as AuthDashboardRouteImport } from './routes/_auth/dashboard/route'
import { Route as AuthDashboardUsersImport } from './routes/_auth/dashboard/users'
import { Route as AuthDashboardTasksImport } from './routes/_auth/dashboard/tasks'
import { Route as AuthDashboardProjectsImport } from './routes/_auth/dashboard/projects'
import { Route as AuthDashboardInicioImport } from './routes/_auth/dashboard/inicio'
import { Route as AuthDashboardInboxImport } from './routes/_auth/dashboard/inbox'

// Create/Update Routes

const AuthRouteRoute = AuthRouteImport.update({
  id: '/_auth',
  getParentRoute: () => rootRoute,
} as any)

const IndexRoute = IndexImport.update({
  id: '/',
  path: '/',
  getParentRoute: () => rootRoute,
} as any)

const AuthDashboardRouteRoute = AuthDashboardRouteImport.update({
  id: '/dashboard',
  path: '/dashboard',
  getParentRoute: () => AuthRouteRoute,
} as any)

const AuthDashboardUsersRoute = AuthDashboardUsersImport.update({
  id: '/users',
  path: '/users',
  getParentRoute: () => AuthDashboardRouteRoute,
} as any)

const AuthDashboardTasksRoute = AuthDashboardTasksImport.update({
  id: '/tasks',
  path: '/tasks',
  getParentRoute: () => AuthDashboardRouteRoute,
} as any)

const AuthDashboardProjectsRoute = AuthDashboardProjectsImport.update({
  id: '/projects',
  path: '/projects',
  getParentRoute: () => AuthDashboardRouteRoute,
} as any)

const AuthDashboardInicioRoute = AuthDashboardInicioImport.update({
  id: '/inicio',
  path: '/inicio',
  getParentRoute: () => AuthDashboardRouteRoute,
} as any)

const AuthDashboardInboxRoute = AuthDashboardInboxImport.update({
  id: '/inbox',
  path: '/inbox',
  getParentRoute: () => AuthDashboardRouteRoute,
} as any)

// Populate the FileRoutesByPath interface

declare module '@tanstack/react-router' {
  interface FileRoutesByPath {
    '/': {
      id: '/'
      path: '/'
      fullPath: '/'
      preLoaderRoute: typeof IndexImport
      parentRoute: typeof rootRoute
    }
    '/_auth': {
      id: '/_auth'
      path: ''
      fullPath: ''
      preLoaderRoute: typeof AuthRouteImport
      parentRoute: typeof rootRoute
    }
    '/_auth/dashboard': {
      id: '/_auth/dashboard'
      path: '/dashboard'
      fullPath: '/dashboard'
      preLoaderRoute: typeof AuthDashboardRouteImport
      parentRoute: typeof AuthRouteImport
    }
    '/_auth/dashboard/inbox': {
      id: '/_auth/dashboard/inbox'
      path: '/inbox'
      fullPath: '/dashboard/inbox'
      preLoaderRoute: typeof AuthDashboardInboxImport
      parentRoute: typeof AuthDashboardRouteImport
    }
    '/_auth/dashboard/inicio': {
      id: '/_auth/dashboard/inicio'
      path: '/inicio'
      fullPath: '/dashboard/inicio'
      preLoaderRoute: typeof AuthDashboardInicioImport
      parentRoute: typeof AuthDashboardRouteImport
    }
    '/_auth/dashboard/projects': {
      id: '/_auth/dashboard/projects'
      path: '/projects'
      fullPath: '/dashboard/projects'
      preLoaderRoute: typeof AuthDashboardProjectsImport
      parentRoute: typeof AuthDashboardRouteImport
    }
    '/_auth/dashboard/tasks': {
      id: '/_auth/dashboard/tasks'
      path: '/tasks'
      fullPath: '/dashboard/tasks'
      preLoaderRoute: typeof AuthDashboardTasksImport
      parentRoute: typeof AuthDashboardRouteImport
    }
    '/_auth/dashboard/users': {
      id: '/_auth/dashboard/users'
      path: '/users'
      fullPath: '/dashboard/users'
      preLoaderRoute: typeof AuthDashboardUsersImport
      parentRoute: typeof AuthDashboardRouteImport
    }
  }
}

// Create and export the route tree

interface AuthDashboardRouteRouteChildren {
  AuthDashboardInboxRoute: typeof AuthDashboardInboxRoute
  AuthDashboardInicioRoute: typeof AuthDashboardInicioRoute
  AuthDashboardProjectsRoute: typeof AuthDashboardProjectsRoute
  AuthDashboardTasksRoute: typeof AuthDashboardTasksRoute
  AuthDashboardUsersRoute: typeof AuthDashboardUsersRoute
}

const AuthDashboardRouteRouteChildren: AuthDashboardRouteRouteChildren = {
  AuthDashboardInboxRoute: AuthDashboardInboxRoute,
  AuthDashboardInicioRoute: AuthDashboardInicioRoute,
  AuthDashboardProjectsRoute: AuthDashboardProjectsRoute,
  AuthDashboardTasksRoute: AuthDashboardTasksRoute,
  AuthDashboardUsersRoute: AuthDashboardUsersRoute,
}

const AuthDashboardRouteRouteWithChildren =
  AuthDashboardRouteRoute._addFileChildren(AuthDashboardRouteRouteChildren)

interface AuthRouteRouteChildren {
  AuthDashboardRouteRoute: typeof AuthDashboardRouteRouteWithChildren
}

const AuthRouteRouteChildren: AuthRouteRouteChildren = {
  AuthDashboardRouteRoute: AuthDashboardRouteRouteWithChildren,
}

const AuthRouteRouteWithChildren = AuthRouteRoute._addFileChildren(
  AuthRouteRouteChildren,
)

export interface FileRoutesByFullPath {
  '/': typeof IndexRoute
  '': typeof AuthRouteRouteWithChildren
  '/dashboard': typeof AuthDashboardRouteRouteWithChildren
  '/dashboard/inbox': typeof AuthDashboardInboxRoute
  '/dashboard/inicio': typeof AuthDashboardInicioRoute
  '/dashboard/projects': typeof AuthDashboardProjectsRoute
  '/dashboard/tasks': typeof AuthDashboardTasksRoute
  '/dashboard/users': typeof AuthDashboardUsersRoute
}

export interface FileRoutesByTo {
  '/': typeof IndexRoute
  '': typeof AuthRouteRouteWithChildren
  '/dashboard': typeof AuthDashboardRouteRouteWithChildren
  '/dashboard/inbox': typeof AuthDashboardInboxRoute
  '/dashboard/inicio': typeof AuthDashboardInicioRoute
  '/dashboard/projects': typeof AuthDashboardProjectsRoute
  '/dashboard/tasks': typeof AuthDashboardTasksRoute
  '/dashboard/users': typeof AuthDashboardUsersRoute
}

export interface FileRoutesById {
  __root__: typeof rootRoute
  '/': typeof IndexRoute
  '/_auth': typeof AuthRouteRouteWithChildren
  '/_auth/dashboard': typeof AuthDashboardRouteRouteWithChildren
  '/_auth/dashboard/inbox': typeof AuthDashboardInboxRoute
  '/_auth/dashboard/inicio': typeof AuthDashboardInicioRoute
  '/_auth/dashboard/projects': typeof AuthDashboardProjectsRoute
  '/_auth/dashboard/tasks': typeof AuthDashboardTasksRoute
  '/_auth/dashboard/users': typeof AuthDashboardUsersRoute
}

export interface FileRouteTypes {
  fileRoutesByFullPath: FileRoutesByFullPath
  fullPaths:
    | '/'
    | ''
    | '/dashboard'
    | '/dashboard/inbox'
    | '/dashboard/inicio'
    | '/dashboard/projects'
    | '/dashboard/tasks'
    | '/dashboard/users'
  fileRoutesByTo: FileRoutesByTo
  to:
    | '/'
    | ''
    | '/dashboard'
    | '/dashboard/inbox'
    | '/dashboard/inicio'
    | '/dashboard/projects'
    | '/dashboard/tasks'
    | '/dashboard/users'
  id:
    | '__root__'
    | '/'
    | '/_auth'
    | '/_auth/dashboard'
    | '/_auth/dashboard/inbox'
    | '/_auth/dashboard/inicio'
    | '/_auth/dashboard/projects'
    | '/_auth/dashboard/tasks'
    | '/_auth/dashboard/users'
  fileRoutesById: FileRoutesById
}

export interface RootRouteChildren {
  IndexRoute: typeof IndexRoute
  AuthRouteRoute: typeof AuthRouteRouteWithChildren
}

const rootRouteChildren: RootRouteChildren = {
  IndexRoute: IndexRoute,
  AuthRouteRoute: AuthRouteRouteWithChildren,
}

export const routeTree = rootRoute
  ._addFileChildren(rootRouteChildren)
  ._addFileTypes<FileRouteTypes>()

/* ROUTE_MANIFEST_START
{
  "routes": {
    "__root__": {
      "filePath": "__root.tsx",
      "children": [
        "/",
        "/_auth"
      ]
    },
    "/": {
      "filePath": "index.tsx"
    },
    "/_auth": {
      "filePath": "_auth/route.tsx",
      "children": [
        "/_auth/dashboard"
      ]
    },
    "/_auth/dashboard": {
      "filePath": "_auth/dashboard/route.tsx",
      "parent": "/_auth",
      "children": [
        "/_auth/dashboard/inbox",
        "/_auth/dashboard/inicio",
        "/_auth/dashboard/projects",
        "/_auth/dashboard/tasks",
        "/_auth/dashboard/users"
      ]
    },
    "/_auth/dashboard/inbox": {
      "filePath": "_auth/dashboard/inbox.tsx",
      "parent": "/_auth/dashboard"
    },
    "/_auth/dashboard/inicio": {
      "filePath": "_auth/dashboard/inicio.tsx",
      "parent": "/_auth/dashboard"
    },
    "/_auth/dashboard/projects": {
      "filePath": "_auth/dashboard/projects.tsx",
      "parent": "/_auth/dashboard"
    },
    "/_auth/dashboard/tasks": {
      "filePath": "_auth/dashboard/tasks.tsx",
      "parent": "/_auth/dashboard"
    },
    "/_auth/dashboard/users": {
      "filePath": "_auth/dashboard/users.tsx",
      "parent": "/_auth/dashboard"
    }
  }
}
ROUTE_MANIFEST_END */
