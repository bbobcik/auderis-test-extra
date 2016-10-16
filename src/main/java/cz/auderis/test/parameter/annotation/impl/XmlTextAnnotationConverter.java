/*
 * Copyright 2015-2016 Boleslav Bobcik - Auderis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.auderis.test.parameter.annotation.impl;

import cz.auderis.test.parameter.annotation.XmlText;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlTextAnnotationConverter implements Converter<XmlText, Source> {
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z][a-z0-9_-]*", Pattern.CASE_INSENSITIVE);

    String prefix;
    String suffix;
    String schemaUri;

    public XmlTextAnnotationConverter() {
        prefix = "";
        suffix = "";
        schemaUri = "";
    }

    public void initialize(String tag, String prefix, String suffix, String schemaUri) {
        if ((null != tag) && !tag.isEmpty()) {
            final Matcher matcher = ID_PATTERN.matcher(tag);
            final boolean validId = matcher.matches();
            assert validId : "tag value is invalid: " + tag;
            prefix = '<' + tag + '>';
            suffix = "</" + tag + '>';
        }
        if ((null != prefix) && !prefix.isEmpty()) {
            this.prefix = this.prefix + prefix;
        }
        if ((null != suffix) && !suffix.isEmpty()) {
            this.suffix = suffix + this.suffix;
        }
        this.schemaUri = (null != schemaUri) ? schemaUri : "";
    }

    @Override
    public void initialize(XmlText annotation) {
        assert null != annotation;
        initialize(annotation.tag().trim(), annotation.prefix(), annotation.suffix(), annotation.schemaUri());
    }

    @Override
    public Source convert(Object param) throws ConversionFailedException {
        try {
            final String inputText = prefix + param.toString() + suffix;
            // Prepare source based on input text
            final StringReader inReader = new StringReader(inputText);
            final InputSource baseSource = new InputSource(inReader);
            // Create filtered SAX source
            final Source filteredSource = filterSourceNamespace(baseSource);
            return filteredSource;
        } catch (SAXException e) {
            throw new ConversionFailedException(e.getMessage());
        }
    }

    private Source filterSourceNamespace(InputSource src) throws SAXException {
        assert null != src;
        // Create filter for XML namespace processing
        final XMLReader parentReader = XMLReaderFactory.createXMLReader();
        final NamespaceFilter filter = new NamespaceFilter(schemaUri, true);
        filter.setParent(parentReader);
        // Create filtered SAX source
        final SAXSource saxSource = new SAXSource(filter, src);
        return saxSource;
    }

    static class NamespaceFilter extends XMLFilterImpl {
        final String usedNamespaceUri;
        final boolean addNamespace;
        boolean addedNamespace;

        public NamespaceFilter(String namespaceUri, boolean addNamespace) {
            super();
            this.addNamespace = addNamespace;
            if (addNamespace) {
                assert null != namespaceUri;
                this.usedNamespaceUri = namespaceUri;
            } else {
                this.usedNamespaceUri = "";
            }
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            if (addNamespace) {
                startControlledPrefixMapping();
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            super.startElement(this.usedNamespaceUri, localName, qName, attrs);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(this.usedNamespaceUri, localName, qName);
        }

        @Override
        public void startPrefixMapping(String prefix, String url) throws SAXException {
            if (addNamespace) {
                this.startControlledPrefixMapping();
            } else {
                // Remove the namespace, i.e. do not call startPrefixMapping for parent!
            }
        }

        private void startControlledPrefixMapping() throws SAXException {
            if (addedNamespace || !addNamespace) {
                return;
            }
            // We should add namespace since it is set and has not yet been done.
            super.startPrefixMapping("", this.usedNamespaceUri);
            this.addedNamespace = true;
        }
    }

}
