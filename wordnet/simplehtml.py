from google.appengine.ext import db
from wordnet import WordNet
import cgi
import json
import cgitb
cgitb.enable()

print 'Content-Type: text/html\n'
print '<html> <head></head><body>'

def toLink(word):
        return '<a href="simplehtml?word=%s">%s</a>' % (word, word.replace("_", " "))

def printEntry(entry):
    print "<h1>%s</h1><ul>" % (entry.word.replace("_", " "),)

    #print entry.json

    meanings = json.loads(entry.json)
    for meaning in meanings:
        print "<li><em>"
        print meaning["desc"]
        print "</em><br />"
        for word in meaning["words"]:
            print " " + toLink(word),
        print "<ul>"
        for relation in meaning["rel"]:
            print "<li><small>%s:" % (relation,)
            for word in meaning["rel"][relation]:
                print toLink(word)
            print "</small></li>"
        print "</ul>"
        print "</li>"
    print "</ul>"
    print '<a href="simplehtml?word=%s+">index</a>' % (entry.word,)

def body():
    params = cgi.FieldStorage()
    word = params.getfirst("word")
    print """
        <form action="simplehtml" method="GET">
            <input type="text" name="word" />
            <input type="submit" value="search"/>
        </form>
        """
    if word == None:
        return
    entry = db.GqlQuery("SELECT * FROM WordNet WHERE word=:1", word).get()

    if entry != None:
        printEntry(entry)
        return

    entries = db.GqlQuery("SELECT * FROM WordNet WHERE word>:1 ORDER BY word ASC", word).fetch(10)
    entries.extend(db.GqlQuery("SELECT * FROM WordNet WHERE word<:1 ORDER BY word DESC", word).fetch(10))
    entries = [str(x.word) for x in entries]
    entries.sort()
    entries = map(toLink, entries)
    for entry in entries:
        print '<li>%s</li>' % (entry,)
    print '</ul>'

body()
print '<div style="text-align:right;font-size:50%"><a href="LICENSE_FOR_DATABASE">database license</a></div></body></html>'
