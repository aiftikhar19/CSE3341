;Author: Aisha Iftikhar
;Filename: myfns.ss
;Class: CSE 3341
;Synopsis: Create a Scheme interpreter for a PLAN program

;create empty list for bindings
(define b '())
;use myinterpreter to call evaluation function for each PLAN program
(define (myinterpreter list)
	(if (null? list)
		'()
		(cons (myeval (car list) b)(myinterpreter (cdr list))) 
	)
)
;evaluation function to check each expression case
(define (myeval m b) ;(display m) (newline) (display b) (newline)
	(cond 
		;TODO: fix ambiguity of input as list containing or input as only id
		((symbol? m) (myeval (find m b) b))
		((integer? m) m)
		((integer? (car m)) (car m)) 
		;check if input equals any define function name
		((equal? (car m) 'prog) (myeval (car (cdr m)) b))
		((equal? (car m) 'myignore) (evalmyignore (cdr m) b))
		((equal? (car m) 'myadd) (evalmyadd (cdr m) (cdr (cdr m)) b))
		((equal? (car m) 'mymul) (evalmymul (cdr m) (cdr (cdr m)) b))
		((equal? (car m) 'myneg) (evalmyneg (cdr m) b))
		((equal? (car m) 'mylet) (evalmylet (cdr m) (cdr(cdr m)) (cdr (cdr (cdr m))) b))
		((symbol? (car m)) (myeval (find (car m) b) b))
	)
)
;myignore evaluates to integer 0, regardless of what x is
(define (evalmyignore x b) 0)
;myadd evaluates to the sum of x and y
(define (evalmyadd x y b) (+ (myeval (car x) b)(myeval (car y) b)))
;mymul evaluates to product of x and y
(define (evalmymul x y b) (* (myeval (car x) b)(myeval (car y) b)))
;myneg evaluates to x * -1
(define (evalmyneg x b) (* -1 (myeval (car x) b)))
;mylet evaluates y, then stores the binding x y in b, and then evaluates z
(define (evalmylet x y z b) (myeval (car z) (cons (list (car x) (myeval (car y) b)) b)))
;find is used to locate a binding given the paramater
(define (find x b) 
	(if (equal? (car (car b)) x) 
		(cdr (car b)) 
		(find x (cdr b))
	)
)
