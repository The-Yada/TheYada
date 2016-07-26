
module.exports = function (app) {

    app.filter('searchFor', function(){
        return function(arr, searchString){
            if(!searchString){
                return arr;
            }
            var result = [];
            searchString = searchString.toLowerCase();
            angular.forEach(arr, function(item){

                item.yadaList.forEach(function(e){

                      if(e.content.toLowerCase().indexOf(searchString) !== -1){
                        console.log(e);
                      // result.push(e);
                  }
                })
      
            });
            return result;
        };
    });
}
