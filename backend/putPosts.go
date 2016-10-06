package main

import (
	"encoding/json"
	"log"
	"net/http"
	"github.com/drone/routes"
	_ "github.com/go-sql-driver/mysql"
    	"database/sql"
    	"fmt"
	"strings"
	"strconv"
	"gopkg.in/gomail.v2"
)

type Posting struct{
	PostID 			string 		`json:"mPostId"`
	Street	 		string 		`json:"mStreet"`
	City	 		string 		`json:"mCity"`
	State	 		string 		`json:"mState"`
	Zip	 		string 		`json:"mZip"`
	Type	 		string 		`json:"mType"`
	Rooms	 		string 		`json:"mRooms"`
	Baths 			string 		`json:"mBaths"`
	Sqft	 		string 		`json:"mSqft"`
	Rent	 		string 		`json:"mRent"`
	Phone	 		string 		`json:"mPhone"`
	Email	 		string 		`json:"mEmail"`
	Description	 	string 		`json:"mDescription"`
	Counter	 		int 		`json:"mCounter"`
	Status			string		`json:"mStatus"`
} 

type Favor struct{
	Email	string		`json:"mEmail"`
	Posts	string		`json:"posts"`
}


type SavedSearch struct { 
	Email 	string	`json:"mEmail"`
	Ssearch	string	`json:"Ssearch"`
}

type Favorite struct { 
	Email 	string	`json:"mEmail"`
	IsFav	string	`json:"isFav"`
}

func checkErr(err error) {
    if err != nil {
        panic(err)
    }
}


func main() {
	mux := routes.New()

	//posting new post
	mux.Post("/rent", PostValue) 		

	//updating old post
	mux.Put("/rent/:postID", PutValue)

	//deleting old post
	mux.Del("/rent/:postID", DeleteValue)

	//Get all the values in rent table
	mux.Get("/rent",GetAllRentValues)

	//Get all the posts of the owner
	mux.Get("/rent/:email",GetAllOwnerPosts)

	//Nikhil: Increase counter for every get
	//Get individual item for display	
	mux.Get("/:postID", GetDetails)	
	
	//Get favs of particular tenant
	mux.Get("/favorites/:email", GetFavorite)
	
	//Get whether particular post is fav of particular tenant
	mux.Get("/isfavorite/:userEmail/:postID", IsFavorite)

	//Put favs of particular tenant
	mux.Put("/favorites/:userEmail/:postID", PutFavorite)

	//Delete fav of particular tenant particular
	mux.Del("/favorites/:userEmail/:postID", DeleteFavorite)		

	//Search  
	mux.Get("/:keyword/:location/:propertyType/:priceRange",GetValues)

	//put saved search list		
	mux.Put("/savedsearch/:email/:keyword/:location/:propertyType/:priceRange",PutSavedSearch)

	//Saved search	add some identifier		
	mux.Get("/savedsearch/:email",GetSavedSearch)

	//Delete saved search list		
	mux.Del("/savedsearch/:email/:keyword/:location/:propertyType/:priceRange",DeleteSavedSearch)

	http.Handle("/", mux)
	log.Println("Listening...")
	http.ListenAndServe(":3000", nil)
}


func DeleteValue(w http.ResponseWriter, r *http.Request) {
	//Nikhil: send postID as key

	params := r.URL.Query()
	postID := params.Get(":postID")
	
	var posting Posting
	
	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	st, err := db.Prepare("select * from rent where mPostId like ?")
	checkErr(err)

    	rows, err := st.Query(postID)
    	checkErr(err)

	for rows.Next() {
        err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 		&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        checkErr(err)
	}

	//Send the mail 
	//[Begin Mail code]
	m := gomail.NewMessage()
    	m.SetHeader("From", "vishwas_513@yahoo.co.in")
    	m.SetHeader("To", posting.Email)

    	m.SetHeader("Subject", "Post Updated")
    	m.SetBody("text/html", "Hello</b>Your post is deleted")

    	d := gomail.NewDialer("smtp.mail.yahoo.com", 587, "vishwas_513@yahoo.co.in", "f22raptor")

    	if err := d.DialAndSend(m); err != nil {
        panic(err)
    	}
	//[End Mail code]
	

	// delete old post	
	stmt, err := db.Prepare("delete from rent where mPostId=?")
	checkErr(err)

    	stmt.Exec(postID)
    	checkErr(err)

    	db.Close()
	w.WriteHeader(http.StatusOK)
}

func PostValue(w http.ResponseWriter, r *http.Request) {

	//Nikhil: send email as key
	fmt.Println(r.Header.Get("Content-Type"))
	decoder := json.NewDecoder(r.Body)
	var posting Posting

	err := decoder.Decode(&posting)
	checkErr(err)

	fmt.Println(posting.PostID)
	fmt.Println(posting.Email)
	//Send the mail 
	//[Begin Mail code]
	m := gomail.NewMessage()
    	m.SetHeader("From", "vishwas_513@yahoo.co.in")
    	m.SetHeader("To", posting.Email)

    	m.SetHeader("Subject", "New Post Created")
    	m.SetBody("text/html", "Hello</b>You created a new post")


    	d := gomail.NewDialer("smtp.mail.yahoo.com", 587, "vishwas_513@yahoo.co.in", "f22raptor")

    	// Send the email to Bob, Cora and Dan.
    	if err := d.DialAndSend(m); err != nil {
        panic(err)
    	}
	//[End Mail code]

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

    	//Insert New Post
	stmt, err := db.Prepare("INSERT rent SET mPostId=?, mStreet=?, mCity=?, mState=?, mZip=?, mType=?, mRooms=?, mBaths=?, mSqft=?, 				mRent=?, mPhone=?,mEmail=?,mDescription=?,mCounter=?,mStatus=?")
	checkErr(err)

    	res, err := stmt.Exec(posting.PostID, posting.Street, posting.City, posting.State, posting.Zip, posting.Type, posting.Rooms, 		posting.Baths, posting.Sqft, posting.Rent, posting.Phone, posting.Email, posting.Description, posting.Counter, posting.Status)
    	checkErr(err)

    	id, err := res.LastInsertId()
    	checkErr(err)

    	fmt.Println(id)

    	db.Close()
	w.WriteHeader(http.StatusCreated)
}

func PutValue(w http.ResponseWriter, r *http.Request) {
	//Nikhil: send postID as key

	fmt.Println(r.Header.Get("Content-Type"))
	decoder := json.NewDecoder(r.Body)
	var posting Posting

	err := decoder.Decode(&posting)
	checkErr(err)

	
	params := r.URL.Query()
	postID := params.Get(":postID")

	fmt.Println(posting.PostID)
	//Send the mail 
	//[Begin Mail code]
	m := gomail.NewMessage()
    	m.SetHeader("From", "vishwas_513@yahoo.co.in")
    	m.SetHeader("To", posting.Email)

    	m.SetHeader("Subject", "Post Updated")
    	m.SetBody("text/html", "Hello</b>Your post is updated")

    	d := gomail.NewDialer("smtp.mail.yahoo.com", 587, "vishwas_513@yahoo.co.in", "f22raptor")

    	if err := d.DialAndSend(m); err != nil {
        panic(err)
    	}
	//[End Mail code]

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)


	// Update old post	
	stmt, err := db.Prepare("UPDATE rent SET  mStreet=?, mCity=?, mState=?, mZip=?, mType=?, mRooms=?, mBaths=?, mSqft=?, 				mRent=?, mPhone=?,mEmail=?,mDescription=?,mCounter=?, mStatus=? where mPostId=?")
	checkErr(err)

    	stmt.Exec(posting.Street, posting.City, posting.State, posting.Zip, posting.Type, posting.Rooms, posting.Baths, 			posting.Sqft, posting.Rent, posting.Phone, posting.Email, posting.Description, posting.Counter,posting.Status, postID)
    	checkErr(err)

    	db.Close()
	w.WriteHeader(http.StatusOK)
}


func GetAllRentValues(w http.ResponseWriter, r *http.Request) {

	var posting Posting
	var results []Posting

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	stmt, err := db.Prepare("select * from rent")
	checkErr(err)

    	rows, err := stmt.Query()
    	checkErr(err)

	for rows.Next() {
        err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 		&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        checkErr(err)

	//put results to put in response
	results = append(results,posting)
        }
	
	db.Close()

    if err := json.NewEncoder(w).Encode(results); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}


func GetAllOwnerPosts(w http.ResponseWriter, r *http.Request) {

	params := r.URL.Query()
	email := params.Get(":email")
	
	var posting Posting
	var results []Posting

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	stmt, err := db.Prepare("select * from rent where mEmail=?")
	checkErr(err)

    	rows, err := stmt.Query(email)
    	checkErr(err)

	for rows.Next() {
        err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 		&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        checkErr(err)

	//put results to put in response
	results = append(results,posting)
        }
	
	db.Close()

    if err := json.NewEncoder(w).Encode(results); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}



func GetDetails(w http.ResponseWriter, r *http.Request) {
	//Nikhil: send postID as key
	
	params := r.URL.Query()
	postID := params.Get(":postID")
	
	var posting Posting

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	stmt, err := db.Prepare("select * from rent where mPostId=?")
	checkErr(err)

    	rows, err := stmt.Query(postID)
    	checkErr(err)

	for rows.Next() {
        err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 		&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter,&posting.Status)
        checkErr(err)

	//update the counter for every view

	stm, err := db.Prepare("UPDATE rent SET mCounter=? where mPostId=?")
	checkErr(err)

    	stm.Exec((posting.Counter+1),posting.PostID)
	
	}
	
	db.Close()

    if err := json.NewEncoder(w).Encode(posting); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}


func GetFavorite(w http.ResponseWriter, r *http.Request) {
	//Nikhil: send (tenant) email as key
	
	params := r.URL.Query()
	email := params.Get(":email")
	
	var posting Posting
	var results []Posting

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	stmt, err := db.Prepare("select * from favourites where mEmail=?")
	checkErr(err)

    	rows, err := stmt.Query(email)
    	checkErr(err)

	for rows.Next() {
        var mail string
        var favs string
        err = rows.Scan(&mail, &favs)
        checkErr(err)
	
	fmt.Println(favs)

	if(len(favs) == 0 ){
	 r.Header.Add("Content-Length", "0")	
	}else {
	//split favs into individual favs
	for _, fav := range strings.Split(favs,","){
	
	st, err := db.Prepare("select * from rent where mPostId=?")
	checkErr(err)

	row, err := st.Query(fav)
    	checkErr(err)

	for row.Next(){
	err = row.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 		&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, &posting.Status)
        checkErr(err)

	//put results to put in response
	results = append(results,posting)
	}
	}
    	}
	}
	db.Close()

    if err := json.NewEncoder(w).Encode(results); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}


func IsFavorite(w http.ResponseWriter, r *http.Request) {
	//Nikhil: send (tenant) email and postid as keys 
	//isfavorite/:userEmail/:postID
	
	params := r.URL.Query()
	userEmail := params.Get(":userEmail")
	postID := params.Get(":postID")
	
	var favorite Favorite
	favorite.IsFav = "false"
	favorite.Email = userEmail

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	stmt, err := db.Prepare("select * from favourites where mEmail=?")
	checkErr(err)

    	rows, err := stmt.Query(userEmail)
    	checkErr(err)

	for rows.Next() {

        var favs string

        err = rows.Scan(&favorite.Email, &favs)
        checkErr(err)
	
	fmt.Println(favs)

	if(len(favs) != 0 ){
	//split favs into individual favs
	for _, fav := range strings.Split(favs,","){
	
	if(fav == postID){
	favorite.IsFav = "true"
	}
	}
	}// end of if
    	}// end of for
	
	db.Close()

    if err := json.NewEncoder(w).Encode(favorite); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}




func PutFavorite(w http.ResponseWriter, r *http.Request) {

	//Nikhil: send (tenant) email as key and Post-ID as value

	params := r.URL.Query()
	userEmail := params.Get(":userEmail")
	var count int	
	var favs string
	var postID string
	postID = params.Get(":postID")

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	//Check if user saved any favorites before
	s, err := db.Prepare("select count(*) as count from favourites where mEmail=?")
	checkErr(err)

	resultRow, err := s.Query(userEmail)
    	checkErr(err)

	for resultRow.Next(){
	err = resultRow.Scan(&count)
        checkErr(err)
	}


	if count ==0 {
	// first favorite of the user so Insert it in DB
	
	favs = postID

	query, err := db.Prepare("INSERT favourites SET mEmail=?,posts=?")
	checkErr(err)

    	res, err := query.Exec(userEmail, favs)
    	checkErr(err)

    	id, err := res.LastInsertId()
    	checkErr(err)

    	fmt.Println(id)

	}else{
	//get old post and append newly marked favourite
	st, err := db.Prepare("select * from favourites where mEmail=?")
	checkErr(err)

	row, err := st.Query(userEmail)
    	checkErr(err)

	for row.Next(){
	var email string

	err = row.Scan(&email, &favs)
        checkErr(err)

	if(len(favs) == 0 ){
		favs = postID
		}else{
		favs = favs+","+postID
		}

	// Update old favs	
	stmt, err := db.Prepare("UPDATE favourites SET posts=? where mEmail=?")
	checkErr(err)

    	stmt.Exec(favs,email)
	}
	}


    	db.Close()
	w.WriteHeader(http.StatusOK)
}


func DeleteFavorite(w http.ResponseWriter, r *http.Request) {

	//favorites/:userEmail/:postID
	
	params := r.URL.Query()
	userEmail := params.Get(":userEmail")
	postID := params.Get(":postID")

	var count int	
	var favs string

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	//Check if user saved any favorites before
	s, err := db.Prepare("select count(*) as count from favourites where mEmail=?")
	checkErr(err)

	resultRow, err := s.Query(userEmail)
    	checkErr(err)

	for resultRow.Next(){
	err = resultRow.Scan(&count)
        checkErr(err)
	}


	if count !=0 {
	//get old favs and remove the specified post 
	
	st, err := db.Prepare("select * from favourites where mEmail=?")
	checkErr(err)

	row, err := st.Query(userEmail)
    	checkErr(err)

	for row.Next(){
	var email string
	var finalfavs string

	err = row.Scan(&email, &favs)
        checkErr(err)

	if(len(favs) != 0 ){
		for _, fav := range strings.Split(favs,","){
		if (fav == postID){
		//remove this postID by skipping it	
	
		}else{
		if (len(finalfavs)== 0){
		finalfavs = fav
		}else{
		finalfavs = finalfavs+","+fav
		}		
		}		
		}		
		}

	// Update old favs to finalfavs
	stmt, err := db.Prepare("UPDATE favourites SET posts=? where mEmail=?")
	checkErr(err)

    	stmt.Exec(finalfavs,email)
	}
	}


    	db.Close()
	w.WriteHeader(http.StatusOK)
}



func GetValues(w http.ResponseWriter, r *http.Request) {
	///:keyword/:location/:propertyType/:priceRange

	//Nikhil: func MatchString(pattern string, s string) (matched bool, err error)

	params   := r.URL.Query()
	keyword  := params.Get(":keyword")
	location := params.Get(":location")
	propertyType := params.Get(":propertyType")
	priceRange := params.Get(":priceRange")
	
	var posting Posting
	var results []Posting

	var queryString string
	var loc string
	var lowPrice int
	var highPrice int
	
	status := 0

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	
	/*
		empty string is replaced by word "foo" in app for ease of implementation
		skip where clause if particular field is empty
	 	status = 1 -> means keyword is empty
	 	status = 2 -> means propertyType is empty
	 	status = 4 -> means priceRange is empty	
		different combination of these gives different queries	
	*/
	if(keyword == "foo"){
		status += 1
	}
	if(propertyType == "foo"){
		status += 2
	}
	if(priceRange == "foo"){
		status += 4
	}else{
	lowPrice,_ = strconv.Atoi(strings.Split(priceRange,"-")[0])
	highPrice,_ = strconv.Atoi(strings.Split(priceRange,"-")[1])

	fmt.Println(lowPrice)
	fmt.Println(highPrice)
	
	}

	switch(status){
	case 1: //only keyword is missing
		queryString = "select * from rent where ((mCity like ?)and (mType=?)"+
				" and (mRent Between ? and ? ));"
		
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)
		
		loc = "%"+location+"%"

    		rows, err := stmt.Query(loc,propertyType,lowPrice,highPrice)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 2: //only propertyType is missing
		queryString = "select * from rent where ((mDescription like ?) and (mCity like ?)"+
				" and (mRent Between ? and ? ));"
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc,lowPrice,highPrice)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 3: //keyword and propertyType is missing
		queryString = "select * from rent where (mCity like ?)"+
				" and (mRent Between ? and ? );"
		
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		loc = "%"+location+"%"

    		rows, err := stmt.Query(loc,lowPrice,highPrice)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 4: // only price range is empty

		queryString = "select * from rent where (mDescription like ?) and (mCity like ?)and (mType=?);"
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc,propertyType)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}	
		break

	case 5: //only property type is available 
		queryString = "select * from rent where  (mType=?) and (mCity like ?);"

		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		loc = "%"+location+"%"

    		rows, err := stmt.Query(propertyType,loc)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}
		
		break

	case 6: //only keyword is available
		queryString = "select * from rent where mDescription like ? and mCity like ?"
				
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)
		
		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 7: // only location is provided
		queryString = "select * from rent where (mCity like ?);"	
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		loc = "%"+location+"%"

    		rows, err := stmt.Query(loc)
    		checkErr(err)
		
		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	default: // All filters are present
		queryString = "select * from rent where ((mDescription like ?) and (mCity like ?)and (mType=?)"+
				" and (mRent Between ? and ? ));"
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc,propertyType,lowPrice,highPrice)
    		checkErr(err)
	
		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break
	}

	db.Close()

    if err := json.NewEncoder(w).Encode(results); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}

func PutSavedSearch(w http.ResponseWriter, r *http.Request) {

	//Nikhil: send (tenant) email as key and all search values
	// foo if any of the search values is empty

	params := r.URL.Query()
	email := params.Get(":email")
	keyword := params.Get(":keyword")
	location := params.Get(":location")
	propertyType := params.Get(":propertyType")
	priceRange := params.Get(":priceRange")

	var count int

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	//Check if user saved any searches before
	s, err := db.Prepare("select count(*) as count from savedsearch where mEmail=?")
	checkErr(err)

	resultRow, err := s.Query(email)
    	checkErr(err)

	for resultRow.Next(){
	err = resultRow.Scan(&count)
        checkErr(err)
	}

	if count ==0 {
	// first saved search of the user so Insert it in DB
	
	var save string

	save = keyword+","+location+","+propertyType+","+priceRange

	query, err := db.Prepare("INSERT savedsearch SET mEmail=?,Ssearch=?")
	checkErr(err)

    	res, err := query.Exec(email, save)
    	checkErr(err)

    	id, err := res.LastInsertId()
    	checkErr(err)

    	fmt.Println(id)

	}else{

	//get old post and append newly saved search
	st, err := db.Prepare("select * from savedsearch where mEmail=?")
	checkErr(err)

	row, err := st.Query(email)
    	checkErr(err)

	for row.Next(){

	var email string
	var searches string

	err = row.Scan(&email, &searches)
        checkErr(err)

	
	if(len(searches) == 0 ){
		searches = keyword+","+location+","+propertyType+","+priceRange
		}else{
		searches = searches+";"+keyword+","+location+","+propertyType+","+priceRange
		}

	// append to old searches	
	stmt, err := db.Prepare("UPDATE savedsearch SET Ssearch=? where mEmail=?")
	checkErr(err)

    	stmt.Exec(searches,email)
	}
	}

    	db.Close()
	w.WriteHeader(http.StatusOK)
}

func GetSavedSearch(w http.ResponseWriter, r *http.Request) {
	//Nikhil: send (tenant) email as key
	
	params := r.URL.Query()
	email := params.Get(":email")
	
	var posting Posting
	var results []Posting

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	// query
	stmt, err := db.Prepare("select * from savedsearch where mEmail=?")
	checkErr(err)

    	rows, err := stmt.Query(email)
    	checkErr(err)

	for rows.Next() {
        var mail string
        var searches string
        err = rows.Scan(&mail, &searches)
        checkErr(err)
	
	fmt.Println(searches)

	if(len(searches) == 0 ){
	 r.Header.Add("Content-Length", "0")	
	}else {
	//split searches into individual searches
	for _, search := range strings.Split(searches,";"){
	// Each search is made of 4 different filters
	///:keyword/:location/:propertyType/:priceRange

	keyword  := strings.Split(search,",")[0]
	location := strings.Split(search,",")[1]
	propertyType := strings.Split(search,",")[2]
	priceRange := strings.Split(search,",")[3]


	var queryString string
	var loc string
	var lowPrice int
	var highPrice int
	
	status := 0
	
	/*
		empty string is replaced by word "foo" in app for ease of implementation
		skip where clause if particular field is empty
	 	status = 1 -> means keyword is empty
	 	status = 2 -> means propertyType is empty
	 	status = 4 -> means priceRange is empty	
		different combination of these gives different queries	
	*/

	if(keyword == "foo"){
		status += 1
	}
	if(propertyType == "foo"){
		status += 2
	}
	if(priceRange == "foo"){
		status += 4
	}else{
	lowPrice,_ = strconv.Atoi(strings.Split(priceRange,"-")[0])
	highPrice,_ = strconv.Atoi(strings.Split(priceRange,"-")[1])

	fmt.Println(lowPrice)
	fmt.Println(highPrice)
	}

	switch(status){
	case 1: //only keyword is missing
		queryString = "select * from rent where ((mCity like ?)and (mType=?)"+
				" and (mRent Between ? and ? ));"
		
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)
		
		loc = "%"+location+"%"

    		rows, err := stmt.Query(loc,propertyType,lowPrice,highPrice)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 2: //only propertyType is missing
		queryString = "select * from rent where ((mDescription like ?) and (mCity like ?)"+
				" and (mRent Between ? and ? ));"
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc,lowPrice,highPrice)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 3: //keyword and propertyType is missing
		queryString = "select * from rent where (mCity like ?)"+
				" and (mRent Between ? and ? );"
		
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		loc = "%"+location+"%"

    		rows, err := stmt.Query(loc,lowPrice,highPrice)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 4: // only price range is empty

		queryString = "select * from rent where (mDescription like ?) and (mCity like ?)and (mType=?);"
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc,propertyType)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}	
		break

	case 5: //only property type is available 
		queryString = "select * from rent where  (mType=?) and (mCity like ?);"

		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		loc = "%"+location+"%"

    		rows, err := stmt.Query(propertyType,loc)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}
		
		break

	case 6: //only keyword is available
		queryString = "select * from rent where mDescription like ? and mCity like ?"
				
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)
		
		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc)
    		checkErr(err)

		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	case 7: // only location is provided
		queryString = "select * from rent where (mCity like ?);"	
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		loc = "%"+location+"%"

    		rows, err := stmt.Query(loc)
    		checkErr(err)
		
		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break

	default: // All filters are present
		queryString = "select * from rent where ((mDescription like ?) and (mCity like ?)and (mType=?)"+
				" and (mRent Between ? and ? ));"
		// query
		stmt, err := db.Prepare(queryString)
		checkErr(err)

		pattern := "%"+keyword+"%"
		loc = "%"+location+"%"

    		rows, err := stmt.Query(pattern,loc,propertyType,lowPrice,highPrice)
    		checkErr(err)
	
		for rows.Next() {
		err = rows.Scan(&posting.PostID, &posting.Street, &posting.City, &posting.State, &posting.Zip, &posting.Type, &posting.Rooms, 			&posting.Baths, &posting.Sqft, &posting.Rent, &posting.Phone, &posting.Email, &posting.Description, &posting.Counter, 			&posting.Status)
        	checkErr(err)

		//put results to put in response
		results = append(results,posting)
		}

		break
	}
	}
	}
	}

	db.Close()

    if err := json.NewEncoder(w).Encode(results); err != nil {
		panic(err)
	}

	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
}

func DeleteSavedSearch(w http.ResponseWriter, r *http.Request) {

	//Nikhil: send (tenant) email as key and all search values
	// foo if any of the search values is empty

	params := r.URL.Query()
	email := params.Get(":email")
	keyword := params.Get(":keyword")
	location := params.Get(":location")
	propertyType := params.Get(":propertyType")
	priceRange := params.Get(":priceRange")

	db, err := sql.Open("mysql", "root:narutoteam123@tcp(localhost:3306)/cmpe277?charset=utf8")
    	checkErr(err)

	//get old post and delete particular saved search
	st, err := db.Prepare("select * from savedsearch where mEmail=?")
	checkErr(err)

	row, err := st.Query(email)
    	checkErr(err)

	for row.Next(){

	var email string
	var searches string
	var finalSearches string

	err = row.Scan(&email, &searches)
        checkErr(err)

	if(len(searches) != 0 ){
		
		for _, search := range strings.Split(searches,";"){

		tempKeyword  := strings.Split(search,",")[0]
		tempLocation := strings.Split(search,",")[1]
		tempPropertyType := strings.Split(search,",")[2]
		tempPriceRange := strings.Split(search,",")[3]
		
		if (tempKeyword == keyword && tempLocation == location && tempPropertyType == propertyType && tempPriceRange == priceRange){
		//remove this postID by skipping it	
	
		}else{
		if (len(finalSearches)== 0){
		finalSearches = tempKeyword+","+tempLocation+","+tempPropertyType+","+tempPriceRange
		}else{
		finalSearches = finalSearches+";"+tempKeyword+","+tempLocation+","+tempPropertyType+","+tempPriceRange
		}
		} // end of else
		} // end of for

	// append to old searches	
	stmt, err := db.Prepare("UPDATE savedsearch SET Ssearch=? where mEmail=?")
	checkErr(err)

    	stmt.Exec(finalSearches,email)
	} // end of if
	} // end of for 

    	db.Close()
	w.WriteHeader(http.StatusOK)
}
