<!---freshmark shields
output = [
	link(shield('Bintray', 'bintray', 'tersesystems:terse-logback', 'blue'), 'https://bintray.com/tersesystems/maven/terse-logback/view'),
	link(shield('Latest version', 'latest', '{{version}}', 'blue'), 'https://github.com/tersesystems/terse-logback/releases/latest'),
	link(shield('License CC0', 'license', 'CC0', 'blue'), 'https://tldrlegal.com/license/creative-commons-cc0-1.0-universal'),
	'',
	link(shield('Release Notes', 'release-notes', '{{version}}', 'brightgreen'), 'docs/release-notes.md'),
	link(image('Travis CI', 'https://travis-ci.org/tersesystems/terse-logback.svg?branch=master'), 'https://travis-ci.org/tersesystems/terse-logback')
	].join('\n')
-->
[![Bintray](https://img.shields.io/badge/bintray-tersesystems%3Aterse--logback-blue.svg)](https://bintray.com/tersesystems/maven/terse-logback/view)
[![Latest version](https://img.shields.io/badge/latest-0.15.2-blue.svg)](https://github.com/tersesystems/terse-logback/releases/latest)
[![License CC0](https://img.shields.io/badge/license-CC0-blue.svg)](https://tldrlegal.com/license/creative-commons-cc0-1.0-universal)

[![Release Notes](https://img.shields.io/badge/release--notes-0.15.2-brightgreen.svg)](docs/release-notes.md)
[![Travis CI](https://travis-ci.org/tersesystems/terse-logback.svg?branch=master)](https://travis-ci.org/tersesystems/terse-logback)
<!---freshmark /shields -->

# Terse Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.  

It shows how you configure Logback, and how you can reduce the amount of complexity in your end projects by packaging your logging appenders and configurators in a distinct project.

I've written about the reasoning and internal architecture in a series of blog posts.  The [full list](https://tersesystems.com/category/logging/) is available on [https://tersesystems.com](https://tersesystems.com).
