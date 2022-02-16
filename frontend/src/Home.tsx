import React, { SyntheticEvent, useEffect, useState } from 'react';
import { allFoodsList } from './Services';
import { ToastContainer, toast } from 'react-toastify';
import { IFoods, ResultFoods } from './models/IFood';
import FoodsItem from './components/FoodsItem';
import { Grid, Icon, Input, Label, Pagination, PaginationProps, Select } from 'semantic-ui-react';
import { categories } from './Datas';
import SiteMenu from './components/SiteMenu';

export default function Home() {


  const [foodsArr, setFoodsArr] = useState<ResultFoods[]>([]);
  const [searchArr, setSearchArr] = useState<ResultFoods[]>([]);

  // select category
  const [selectCategory, setSelectCategory] = useState(0)
  const [searchData, setSearchData] = useState("")

  // pages
  const [pageCount, setPageCount] = useState(0);
  const [postsperpage, setPostsPerPage] = useState(5)
  const [currentPage, setCurrentPage] = useState<number>(1);
  const indexOfLastPost = currentPage * postsperpage;
  const indexOfFirstPost = indexOfLastPost- postsperpage;
  var currentpost = foodsArr.slice(indexOfFirstPost,indexOfLastPost); 

  useEffect(() => {
    
    toast.loading("Yükleniyor.")
    allFoodsList().then( res => {
        const dt:IFoods = res.data;
        setFoodsArr( dt.result! )
        setSearchArr( dt.result! )
        if(Math.round(dt.result!.length%postsperpage) === 0){
          setPageCount(dt.result!.length /postsperpage)            
       }else{           
          setPageCount(Math.ceil(dt.result!.length/postsperpage))              
        } 
        toast.dismiss();
    }).catch( err => {
        toast.dismiss();
        toast.error( ""+err )
    })

  }, []);


  const search = ( q:string ) => {
    setCurrentPage(1)
    setSearchData(q)
    if ( q === "" ) {

      var newArr: ResultFoods[] = searchArr
      if ( selectCategory !== 0 ) {
        newArr = newArr.filter( item => item.cid === selectCategory )
      }
      setFoodsArr(newArr)
      if(Math.round(newArr.length%postsperpage) === 0){
        setPageCount(newArr.length /postsperpage)            
      }else{           
        setPageCount(Math.ceil(newArr.length/postsperpage))              
      }


    }else {
      q = q.toLowerCase()
      var newArr = searchArr.filter( item => item.name?.toLowerCase().includes(q) || (""+item.glycemicindex).includes(q) )
      if ( selectCategory !== 0 ) {
        newArr = newArr.filter( item => item.cid === selectCategory )
      }
      setFoodsArr(newArr)
      if(Math.round(newArr.length%postsperpage) === 0){
          setPageCount(newArr.length /postsperpage)            
      }else{           
          setPageCount(Math.ceil(newArr.length/postsperpage))              
        }
    }
  }


  // select cat
  const catOnChange = ( str: string ) => {
    const numCat = parseInt(str)
    setCurrentPage(1)
    setSelectCategory( numCat )

    console.log( numCat )

    var newArr: ResultFoods[] = searchArr
    if ( numCat !== 0 ) {
      newArr = newArr.filter( item => item.cid === numCat )
    }
    
    if ( searchData !== "" ) {
      newArr = newArr.filter( item => item.name?.toLowerCase().includes(searchData) || (""+item.glycemicindex).includes(searchData) )
    }
    setFoodsArr(newArr)

    console.log( newArr )

    if(Math.round(newArr.length%postsperpage) === 0){
      setPageCount(newArr.length /postsperpage)            
    }else{           
        setPageCount(Math.ceil(newArr.length/postsperpage))              
    }

  }


  return (
  <>
  
    <SiteMenu />
    <ToastContainer />
<Grid columns='2'>
<Grid.Row>

<Grid.Column width='8'>
<Grid>
  <Grid.Row >
  <Grid.Column width='14'>
    <Input onChange={(e) => search(e.target.value)} fluid icon='search' iconPosition='left' placeholder='Arama...' />
    </Grid.Column>
    <Grid.Column width='2'>
      <Label circular color='olive' style={{ display:'flex', justifyContent:'center', fontSize:16 }} >
        {foodsArr.length}
      </Label>
    </Grid.Column>
    
  </Grid.Row>
</Grid>
</Grid.Column>


<Grid.Column width='8'>
   <Select onChange={(e, data) => catOnChange( "" +data.value )  } fluid placeholder='Kategori Seç' options={categories} />
</Grid.Column>

</Grid.Row>
</Grid>

    <Grid >
      { currentpost.map((item, index) => 
        <FoodsItem  key={index} item={item} /> 
      )}
      
    </Grid>

    <Grid>
      <Pagination
              activePage={currentPage}              
              //defaultActivePage={currentPage}                                    
              totalPages={pageCount}              
              onPageChange={ (e: SyntheticEvent, {activePage}: PaginationProps) =>  setCurrentPage( parseInt( ""+ activePage!) ) }
            />
    </Grid>
  </>
  );
}