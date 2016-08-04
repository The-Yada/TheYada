The Yada Chrome Extension and Website
================

This includes :

- **AngularJS Chrome Extension:**
`inserted as an iframe, appears like a popup fixed over web content`
- **AngularJS Web Application**
- **GSAP and SASS:** `JS animation library and CSS precompiler`
- **Gulp and npm:** `task runner and package manager`
- **H2 database**
- **Spring Framework**
- **Jsoup**

## :: Get Started ::
- Clone the repository
- In Google Chrome, go to `preferences -> extensions`
- Make sure developer mode is on
- Click load `unpacked extension...`
- select the `/yada-ext directory` from within `/chrome-ext`
- run the `.jar` by running this command in the terminal:  
- (**make sure you have the java runtime environment.**)
```sh
$ java -jar TheYada-0.0.1-SNAPSHOT.jar
```
#####note:
- that the ports are running http://localhost:1776 [website/server]
- to access the **`H2 database`**, go to http://localhost:1777  

#####for development:
- **npm** install should give you all the dependencies [see the `package.json`]
```sh
$ npm install
```
- Gulp is the task runner
```sh
$ gulp; gulp watch
```
## :: Build and deploy ::
-We are not currently deployed yet, but theyada.us is reserved.

## :: Versions ::
- `0.8` July 2016


## :: License ::

Copyright (c) 2016   
Caleb Bodtorf, Will Riggins, and Jon Black


Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
