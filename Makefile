MAINFILE=report

view: $(MAINFILE).pdf $(MAINFILE).tex
	evince $(MAINFILE).pdf

syn: synopsis.pdf synopsis.tex
	evince synopsis.pdf

rap: rapport.pdf rapport.tex
	evince rapport.pdf

section.pdf: section.tex
	pdflatex section
	evince section.pdf

all: $(MAINFILE).pdf section.pdf synopsis.pdf

$(MAINFILE).pdf: $(MAINFILE).tex bibliography.bib */*.tex

synopsis.pdf: synopsis.tex bibliography.bib

.SUFFIXES: .fig .eps .dvi .ps .pdf .tex .dot 

.eps.pdf:
	epstopdf $*.eps

.tex.pdf:
	pdflatex $*.tex && bibtex $* && makeindex $*.idx && pdflatex $*.tex \
	pdflatex $*.tex && pdflatex $*.tex

.tex.dvi:
	latex $*.tex && bibtex $* && makeindex $*.idx && latex $*.tex \
	latex $*.tex && latex $*.tex

.dvi.ps:
	dvips -o $*.ps $*.dvi

.fig.eps:
	fig2dev -L eps  $*.fig $*.eps

.fig.pdf:
	fig2dev -L pdf  $*.fig $*.pdf
	
.dot.eps:
	dot -Tps < $*.dot > $*.eps

clean:
	rm -f *.bbl *.aux *.blg *.log *.dvi *.idx *.ind *.ilg *.toc *.pdf *.ps *.lof *.png *.eps interviews/[0-9]*.tex *.out *.brf ekstraeval/interview.tex